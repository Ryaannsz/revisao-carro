import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import { Maquina } from '../../core/models/maquina.model';
import { MAQUINA_STATUS_INFO, MaquinaStatus, TIPOS_MAQUINA } from '../../core/models/maquina-status.enum';
import { RevisaoMaquina } from '../../core/models/revisao.maquina.model';
import { LeituraHorimetro } from '../../core/models/leitura.horimetro.model';
import { ProximaRevisaoMaquina } from '../../core/models/proxima.revisao.maquina.model';
import { MaquinaService } from '../../core/services/maquina.service';
import { RevisaoMaquinaService } from '../../core/services/maquina.revisao.service';
import { ToastService } from '../../core/services/toast.service';
import { ConfirmationDialogService } from '../../core/services/confirmationDialog.service';
import { AuthService } from '../../core/services/auth.service';
import {
  SITUACAO_INFO, SituacaoPreventiva, detalheMaquina, horasRestantes, nomeMaquina, progressoPreventiva, situacaoPreventiva
} from '../../core/utils/maquina.utils';
import { diasAte } from '../../core/utils/situacao.utils';
import { hojeISO } from '../../core/utils/data.utils';

@Component({
  selector: 'app-maquina-unica',
  standalone: false,
  templateUrl: './maquina-unica.component.html',
  styleUrl: '../carro-unico/carro-unico.component.css'
})
export class MaquinaUnicaComponent implements OnInit {
  id!: number;
  maquina: Maquina | null = null;
  horasAtual = 0;
  revisoes: RevisaoMaquina[] = [];
  leituras: LeituraHorimetro[] = [];
  proxima: ProximaRevisaoMaquina | null = null;
  loading = false;
  aba: 'manutencoes' | 'leituras' = 'manutencoes';
  limite = 10;

  // anotar horímetro
  leituraAberta = false;
  leituraHoras: number | null = null;
  leituraData = hojeISO();
  salvandoLeitura = false;
  readonly hoje = hojeISO();

  // edição
  editando = false;
  edit: Partial<Maquina> = {};

  readonly SIT = SITUACAO_INFO;
  readonly STATUS = MAQUINA_STATUS_INFO;
  readonly statusLista = Object.values(MaquinaStatus);
  readonly tipos = TIPOS_MAQUINA;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private maquinaService: MaquinaService,
    private revisaoService: RevisaoMaquinaService,
    private toastService: ToastService,
    private confirm: ConfirmationDialogService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.carregar();
  }

  carregar() {
    this.loading = true;
    forkJoin({
      m: this.maquinaService.buscar(this.id),
      horas: this.maquinaService.horasRecentes(this.id).pipe(catchError(() => of(0))),
      rev: this.revisaoService.porMaquina(this.id).pipe(catchError(() => of([] as RevisaoMaquina[]))),
      leit: this.maquinaService.leituras(this.id).pipe(catchError(() => of([] as LeituraHorimetro[]))),
      prox: this.revisaoService.proxima(this.id).pipe(catchError(() => of(null))),
    }).subscribe({
      next: ({ m, horas, rev, leit, prox }) => {
        this.maquina = m;
        this.horasAtual = horas || m.horas || 0;
        this.revisoes = rev;
        this.leituras = leit;
        this.proxima = prox;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.toastService.showError(err?.status === 404 ? 'Máquina não encontrada.' : 'Não foi possível carregar a máquina.');
        if (err?.status === 404) this.router.navigate(['/maquinas']);
      }
    });
  }

  /* ---------- Derivados ---------- */

  get nome() { return nomeMaquina(this.maquina); }
  get detalhe() { return detalheMaquina(this.maquina); }
  get status(): MaquinaStatus { return this.maquina?.status ?? MaquinaStatus.DISPONIVEL; }
  get situacao(): SituacaoPreventiva { return situacaoPreventiva(this.proxima); }
  get progresso(): number { return progressoPreventiva(this.proxima); }
  get restante(): number | null { return horasRestantes(this.proxima); }
  get dias(): number | null { return diasAte(this.proxima?.proximaRevisaoEstimada); }
  get intervaloHoras(): number { return this.proxima?.intervaloHoras ?? this.maquina?.alarmeRevisaoHoras ?? 250; }
  get intervaloMeses(): number { return this.maquina?.alarmeRevisaoMes && this.maquina.alarmeRevisaoMes > 0 ? this.maquina.alarmeRevisaoMes : 6; }
  get isAdmin(): boolean { return this.authService.getUserRole(); }

  get horasTrabalhadas(): number {
    return Math.max(0, this.horasAtual - (this.maquina?.horas ?? 0));
  }

  get gastoManutencao(): number {
    return this.revisoes.reduce((t, r) => t + (r.valor ?? 0), 0);
  }

  /** Ritmo médio de uso desde o cadastro (horas por mês). Só aparece com 2+ semanas de histórico. */
  get horasPorMes(): number | null {
    const desde = this.maquina?.dtAdicionado ? diasAte(this.maquina.dtAdicionado) : null;
    if (desde == null || -desde < 14 || !this.horasTrabalhadas) return null;
    return this.horasTrabalhadas / (-desde / 30.44);
  }

  /** Estimativa de quando o horímetro chega na preventiva, no ritmo atual. */
  get previsaoPorHoras(): Date | null {
    const r = this.restante, ritmo = this.horasPorMes;
    if (r == null || r <= 0 || !ritmo) return null;
    const d = new Date();
    d.setDate(d.getDate() + Math.round((r / ritmo) * 30.44));
    return d;
  }

  get revVisiveis() { return this.revisoes.slice(0, this.limite); }
  get leitVisiveis() { return this.leituras.slice(0, this.limite); }
  abs(n: number) { return Math.abs(n); }

  /* ---------- Horímetro ---------- */

  abrirLeitura() {
    this.leituraHoras = null;
    this.leituraData = hojeISO();
    this.leituraAberta = true;
  }

  get leituraMenor(): boolean {
    return this.leituraHoras != null && this.leituraHoras !== ('' as any) && Number(this.leituraHoras) < this.horasAtual;
  }

  salvarLeitura() {
    const h = Number(this.leituraHoras);
    if (this.leituraHoras == null || isNaN(h) || h < 0) return this.toastService.showWarning('Informe as horas do painel.');
    if (this.leituraMenor) return this.toastService.showWarning('O horímetro não pode ser menor que o atual.');
    this.salvandoLeitura = true;
    this.maquinaService.registrarLeitura(this.id, h, this.leituraData).subscribe({
      next: () => {
        this.salvandoLeitura = false;
        this.leituraAberta = false;
        this.toastService.showSuccess(`Horímetro anotado: ${h.toLocaleString('pt-BR')} h.`);
        this.carregar();
      },
      error: (err) => {
        this.salvandoLeitura = false;
        this.toastService.showError(typeof err?.error === 'string' && err.error ? err.error : 'Não foi possível anotar o horímetro.');
      }
    });
  }

  /* ---------- Status / edição / exclusão ---------- */

  mudarStatus(s: MaquinaStatus) {
    if (!this.maquina || s === this.status) return;
    const anterior = this.maquina.status;
    this.maquina.status = s;
    this.maquinaService.atualizarStatus(this.id, s).subscribe({
      next: () => this.toastService.showSuccess(`Situação: ${this.STATUS[s].label.toLowerCase()}.`),
      error: () => {
        if (this.maquina) this.maquina.status = anterior;
        this.toastService.showError('Não foi possível mudar a situação.');
      }
    });
  }

  abrirEdicao() {
    if (!this.maquina) return;
    const m = this.maquina;
    this.edit = {
      identificacao: m.identificacao ?? '', tipo: m.tipo ?? '', ano: m.ano ?? null, chassi: m.chassi ?? '',
      alarmeRevisaoHoras: m.alarmeRevisaoHoras ?? null, alarmeRevisaoMes: m.alarmeRevisaoMes ?? null,
    };
    this.editando = true;
  }

  salvarEdicao() {
    if (!this.edit.identificacao?.trim()) return this.toastService.showWarning('O nome na frota é obrigatório.');
    const dados: Partial<Maquina> = {
      identificacao: this.edit.identificacao.trim(),
      tipo: this.edit.tipo ?? '',
      chassi: this.edit.chassi ?? '',
      ...(this.edit.ano ? { ano: Number(this.edit.ano) } : {}),
      ...(this.edit.alarmeRevisaoHoras ? { alarmeRevisaoHoras: Number(this.edit.alarmeRevisaoHoras) } : {}),
      ...(this.edit.alarmeRevisaoMes ? { alarmeRevisaoMes: Number(this.edit.alarmeRevisaoMes) } : {}),
    };
    this.maquinaService.atualizar(this.id, dados).subscribe({
      next: () => {
        this.editando = false;
        this.toastService.showSuccess('Alterações salvas.');
        this.carregar();
      },
      error: (err) => this.toastService.showError(typeof err?.error === 'string' && err.error ? err.error : 'Não foi possível salvar.')
    });
  }

  excluirManutencao(r: RevisaoMaquina) {
    this.confirm.confirm('Excluir manutenção?', 'A próxima preventiva volta a ser calculada pela manutenção anterior.')
      .subscribe(ok => ok && this.revisaoService.delete(r.id).subscribe({
        next: () => { this.toastService.showSuccess('Manutenção excluída.'); this.carregar(); },
        error: () => this.toastService.showError('Não foi possível excluir a manutenção.')
      }));
  }

  excluirMaquina() {
    this.confirm.confirm(`Excluir ${this.nome}?`, 'Manutenções e leituras de horímetro desta máquina também serão apagadas. Não dá para desfazer.', 'Excluir máquina')
      .subscribe(ok => {
        if (!ok) return;
        this.loading = true;
        this.maquinaService.delete(this.id).subscribe({
          next: () => { this.loading = false; this.toastService.showSuccess('Máquina excluída.'); this.router.navigate(['/maquinas']); },
          error: () => { this.loading = false; this.toastService.showError('Não foi possível excluir a máquina.'); }
        });
      });
  }
}
