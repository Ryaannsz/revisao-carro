import { Component, OnInit } from '@angular/core';
import { catchError, forkJoin, of } from 'rxjs';
import { Maquina } from '../../core/models/maquina.model';
import { MAQUINA_STATUS_INFO, MaquinaStatus } from '../../core/models/maquina-status.enum';
import { ProximaRevisaoMaquina } from '../../core/models/proxima.revisao.maquina.model';
import { MaquinaService } from '../../core/services/maquina.service';
import { RevisaoMaquinaService } from '../../core/services/maquina.revisao.service';
import { ToastService } from '../../core/services/toast.service';
import {
  SITUACAO_INFO, SituacaoPreventiva, detalheMaquina, horasRestantes, nomeMaquina, progressoPreventiva, situacaoPreventiva
} from '../../core/utils/maquina.utils';
import { diasAte, textoPrazo } from '../../core/utils/situacao.utils';

type Filtro = 'TODAS' | 'PREVENTIVA' | MaquinaStatus;

interface LinhaMaquina {
  m: Maquina;
  nome: string;
  detalhe: string;
  status: MaquinaStatus;
  horas: number;
  prox?: ProximaRevisaoMaquina;
  situacao: SituacaoPreventiva;
  progresso: number;
  restante: number | null;
  dias: number | null;
}

const ORDEM: Record<SituacaoPreventiva, number> = { 'vencida': 0, 'proxima': 1, 'ok': 2, 'sem-dados': 3 };

@Component({
  standalone: false,
  selector: 'app-lista-maquinas',
  templateUrl: './lista-maquinas.component.html'
})
export class ListaMaquinasComponent implements OnInit {
  linhas: LinhaMaquina[] = [];
  loading = false;
  erro = false;
  filtro: Filtro = 'TODAS';
  search = '';
  skeletons = Array.from({ length: 4 });

  readonly SIT = SITUACAO_INFO;
  readonly STATUS = MAQUINA_STATUS_INFO;
  readonly statusFiltros: MaquinaStatus[] = [MaquinaStatus.DISPONIVEL, MaquinaStatus.EM_OBRA, MaquinaStatus.MANUTENCAO, MaquinaStatus.PARADA];

  constructor(
    private maquinaService: MaquinaService,
    private revisaoMaquinaService: RevisaoMaquinaService,
    private toastService: ToastService
  ) { }

  ngOnInit(): void {
    this.carregar();
  }

  carregar() {
    this.loading = true;
    this.erro = false;
    forkJoin({
      maquinas: this.maquinaService.listar(),
      proximas: this.revisaoMaquinaService.proximas().pipe(catchError(() => of([] as ProximaRevisaoMaquina[]))),
    }).subscribe({
      next: ({ maquinas, proximas }) => {
        const porMaquina = new Map(proximas.map(p => [p.maquinaId, p]));
        this.linhas = maquinas.map(m => {
          const prox = porMaquina.get(m.id);
          const situacao = situacaoPreventiva(prox);
          return {
            m,
            nome: nomeMaquina(m),
            detalhe: detalheMaquina(m),
            status: m.status ?? MaquinaStatus.DISPONIVEL,
            horas: prox?.horasAtual ?? m.horas ?? 0,
            prox,
            situacao,
            progresso: progressoPreventiva(prox),
            restante: horasRestantes(prox),
            dias: diasAte(prox?.proximaRevisaoEstimada),
          };
        }).sort((a, b) => ORDEM[a.situacao] - ORDEM[b.situacao] || a.nome.localeCompare(b.nome));
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.erro = true;
        this.toastService.showError('Não foi possível carregar as máquinas.');
      }
    });
  }

  private precisaPreventiva(l: LinhaMaquina) {
    return l.situacao === 'vencida' || l.situacao === 'proxima';
  }

  get filtradas(): LinhaMaquina[] {
    const q = this.search.trim().toLowerCase();
    return this.linhas.filter(l => {
      const passa = this.filtro === 'TODAS' ? true
        : this.filtro === 'PREVENTIVA' ? this.precisaPreventiva(l)
        : l.status === this.filtro;
      const busca = !q || [l.nome, l.detalhe, l.m.chassi, l.m.tipo].some(v => (v ?? '').toLowerCase().includes(q));
      return passa && busca;
    });
  }

  contar(f: Filtro): number {
    if (f === 'TODAS') return this.linhas.length;
    if (f === 'PREVENTIVA') return this.linhas.filter(l => this.precisaPreventiva(l)).length;
    return this.linhas.filter(l => l.status === f).length;
  }

  limparFiltros() {
    this.search = '';
    this.filtro = 'TODAS';
  }

  trackBy(_: number, l: LinhaMaquina) { return l.m.id; }
  abs(n: number) { return Math.abs(n); }
  prazo(l: { restante: number | null; dias: number | null }) { return textoPrazo(l.restante, l.dias, 'h', 'Preventiva'); }
}
