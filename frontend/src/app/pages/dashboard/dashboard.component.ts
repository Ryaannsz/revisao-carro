import { Component, OnInit } from '@angular/core';
import { catchError, forkJoin, of } from 'rxjs';
import { DashboardService } from '../../core/services/dashboard.service';
import { CarroService } from '../../core/services/carro.service';
import { MaquinaService } from '../../core/services/maquina.service';
import { RevisaoMaquinaService } from '../../core/services/maquina.revisao.service';
import { MesValor } from '../../core/models/mes.valor.model';
import { DashboardResumo } from '../../core/models/dashboard.resumo.model';
import { Carro } from '../../core/models/carro.model';
import { Maquina } from '../../core/models/maquina.model';
import { ProximaRevisaoMaquina } from '../../core/models/proxima.revisao.maquina.model';
import { AppConstants } from '../../core/constants/app.constants';
import { SERIES } from '../../components/line-chart/line-chart.component';
import {
  SITUACAO_INFO, SituacaoPreventiva, diasAte, kmRestantes, nomeVeiculo, situacaoCarro
} from '../../core/utils/situacao.utils';
import { horasRestantes, nomeMaquina, situacaoPreventiva } from '../../core/utils/maquina.utils';

interface Serie { labels: string[]; datasets: any[]; }

interface Pendencia {
  tipo: 'carro' | 'maquina';
  titulo: string;
  placa: string | null;
  situacao: SituacaoPreventiva;
  restante: number | null;
  unidade: 'km' | 'h';
  data: string | null;
  dias: number | null;
  link: any[];
}

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  standalone: false
})
export class DashboardComponent implements OnInit {
  readonly periodos = [{ v: 3, l: '3 meses' }, { v: 6, l: '6 meses' }, { v: 12, l: '12 meses' }, { v: 0, l: 'Tudo' }];
  periodMonths = 6;
  loading = false;
  erro = false;

  private resumo: DashboardResumo | null = null;
  pendencias: Pendencia[] = [];
  mostrarTodas = false;

  gasto: Serie = { labels: [], datasets: [] };
  litros: Serie = { labels: [], datasets: [] };
  eficiencia: Serie = { labels: [], datasets: [] };
  revisoes: Serie = { labels: [], datasets: [] };

  readonly SIT = SITUACAO_INFO;

  readonly opcoesMoeda = {
    plugins: { tooltip: { callbacks: { label: (c: any) => `${c.dataset.label}: ${this.brl(c.parsed.y)}` } } },
    scales: { y: { ticks: { callback: (v: number) => this.brl(v, 0) } } },
  };
  readonly opcoesNumero = {
    plugins: { tooltip: { callbacks: { label: (c: any) => `${c.dataset.label}: ${c.parsed.y == null ? 'sem dado' : Number(c.parsed.y).toLocaleString('pt-BR', { maximumFractionDigits: 1 })}` } } },
    scales: { y: { ticks: { callback: (v: number) => Number(v).toLocaleString('pt-BR') } } },
  };
  readonly opcoesInteiro = {
    scales: { y: { ticks: { precision: 0 } } },
  };

  constructor(
    private svc: DashboardService,
    private carroService: CarroService,
    private maquinaService: MaquinaService,
    private revisaoMaquinaService: RevisaoMaquinaService
  ) { }

  ngOnInit(): void {
    this.carregar();
  }

  carregar() {
    this.loading = true;
    this.erro = false;
    forkJoin({
      resumo: this.svc.getResumo(),
      carros: this.carroService.getList<Carro>().pipe(catchError(() => of([] as Carro[]))),
      maquinas: this.maquinaService.listar().pipe(catchError(() => of([] as Maquina[]))),
      proxMaq: this.revisaoMaquinaService.proximas().pipe(catchError(() => of([] as ProximaRevisaoMaquina[]))),
    }).subscribe({
      next: ({ resumo, carros, maquinas, proxMaq }) => {
        this.resumo = resumo;
        this.montarPendencias(resumo, carros, maquinas, proxMaq);
        this.montarGraficos();
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.erro = true;
      }
    });
  }

  setPeriod(m: number) {
    this.periodMonths = m;
    this.montarGraficos();
  }

  /* ---------------- KPIs do mês ---------------- */

  private mesChave(offset = 0): string {
    const d = new Date();
    d.setDate(1);
    d.setMonth(d.getMonth() + offset);
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`;
  }

  private valorMes(arr: MesValor[] | undefined, chave: string): number {
    return Number(arr?.find(x => x.mes === chave)?.valor ?? 0) || 0;
  }

  get nomeMesAtual(): string {
    return new Date().toLocaleDateString('pt-BR', { month: 'long' });
  }

  get gastoMes(): number {
    const k = this.mesChave();
    return this.valorMes(this.resumo?.gastoMensal, k) + this.valorMes(this.resumo?.custoManutencaoMensal, k);
  }

  get gastoMesAnterior(): number {
    const k = this.mesChave(-1);
    return this.valorMes(this.resumo?.gastoMensal, k) + this.valorMes(this.resumo?.custoManutencaoMensal, k);
  }

  get nomeMesAnterior(): string {
    const d = new Date(); d.setDate(1); d.setMonth(d.getMonth() - 1);
    return d.toLocaleDateString('pt-BR', { month: 'long' });
  }

  get litrosMes(): number { return this.valorMes(this.resumo?.litrosMensais, this.mesChave()); }

  /** Consumo do último mês FECHADO (o mês corrente ainda está incompleto). */
  private get ultimoKmL(): MesValor | null {
    const atual = this.mesChave();
    const arr = (this.resumo?.eficienciaMensal ?? []).filter(x => x.mes < atual && x.valor != null && Number(x.valor) > 0);
    return arr.length ? arr[arr.length - 1] : null;
  }

  get kmLMes(): number | null { return this.ultimoKmL ? Number(this.ultimoKmL.valor) : null; }
  get kmLMesLabel(): string { return this.ultimoKmL ? this.formatMes(this.ultimoKmL.mes) : ''; }

  get vencidas(): number { return this.pendencias.filter(p => p.situacao === 'vencida').length; }

  get pendenciasVisiveis(): Pendencia[] {
    return this.mostrarTodas ? this.pendencias : this.pendencias.slice(0, 6);
  }

  /* ---------------- Montagem ---------------- */

  private montarPendencias(r: DashboardResumo, carros: Carro[], maquinas: Maquina[], proxMaq: ProximaRevisaoMaquina[]) {
    const carroPorId = new Map(carros.map(c => [c.id, c]));
    const maqPorId = new Map(maquinas.map(m => [m.id, m]));
    const lista: Pendencia[] = [];

    for (const p of r.proximasRevisoes ?? []) {
      const c = carroPorId.get(p.carroId);
      const s = situacaoCarro(c, p, p.kmAtual);
      if (s !== 'vencida' && s !== 'proxima') continue;
      lista.push({
        tipo: 'carro', titulo: c ? nomeVeiculo(c.marca?.marca, c.modelo?.modelo) : p.placa, placa: p.placa,
        situacao: s, restante: kmRestantes(p, p.kmAtual), unidade: 'km',
        data: p.proximaRevisaoEstimada, dias: diasAte(p.proximaRevisaoEstimada), link: ['/carros/unico', p.carroId],
      });
    }
    for (const p of proxMaq) {
      const s = situacaoPreventiva(p);
      if (s !== 'vencida' && s !== 'proxima') continue;
      lista.push({
        tipo: 'maquina', titulo: nomeMaquina(maqPorId.get(p.maquinaId)) || p.identificacao || `Máquina #${p.maquinaId}`, placa: null,
        situacao: s, restante: horasRestantes(p), unidade: 'h',
        data: p.proximaRevisaoEstimada, dias: diasAte(p.proximaRevisaoEstimada), link: ['/maquinas', p.maquinaId],
      });
    }
    const ordem = { 'vencida': 0, 'proxima': 1, 'ok': 2, 'sem-dados': 3 };
    this.pendencias = lista.sort((a, b) => ordem[a.situacao] - ordem[b.situacao] || (a.dias ?? 9999) - (b.dias ?? 9999));
  }

  private montarGraficos() {
    const r = this.resumo;
    if (!r) return;
    this.gasto = this.multi([
      { arr: r.gastoMensal ?? [], label: 'Combustível', cor: SERIES[0] },
      { arr: r.custoManutencaoMensal ?? [], label: 'Manutenção', cor: SERIES[1] },
    ]);
    this.litros = this.multi([{ arr: r.litrosMensais ?? [], label: 'Litros', cor: SERIES[0] }]);
    this.eficiencia = this.multi([{ arr: r.eficienciaMensal ?? [], label: 'km/L', cor: SERIES[0] }], true);
    this.revisoes = this.multi([{ arr: r.revisoesPorMes ?? [], label: 'Revisões', cor: SERIES[0] }]);
  }

  /** Alinha várias séries mensais no mesmo eixo; mês ausente vira 0 (ou null se `gaps`). */
  private multi(series: { arr: MesValor[]; label: string; cor: string }[], gaps = false): Serie {
    let meses = Array.from(new Set(series.flatMap(s => s.arr.map(x => x.mes)))).sort();
    if (this.periodMonths > 0 && meses.length > this.periodMonths) meses = meses.slice(-this.periodMonths);
    return {
      labels: meses.map(m => this.formatMes(m)),
      datasets: series.filter(s => s.arr.length).map(s => {
        const por = new Map(s.arr.map(x => [x.mes, x.valor]));
        return { label: s.label, borderColor: s.cor, data: meses.map(m => {
          const v = por.get(m);
          return gaps ? (v != null && Number(v) > 0 ? v : null) : (v ?? 0);
        }) };
      }),
    };
  }

  private formatMes(mesStr: string): string {
    const [ano, mes] = mesStr.split('-').map(Number);
    if (!ano || !mes) return mesStr;
    return `${AppConstants.MESES_ABREV[mes - 1]}/${String(ano).slice(-2)}`;
  }

  brl(v: number | null | undefined, casas = 2): string {
    if (v == null) return 'sem dado';
    return Number(v).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL', minimumFractionDigits: casas, maximumFractionDigits: casas });
  }

  abs(n: number) { return Math.abs(n); }
}
