import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import { CarroService } from '../../core/services/carro.service';
import { AbastService } from '../../core/services/abast.service';
import { RevisaoService } from '../../core/services/revisao.service';
import { Carro } from '../../core/models/carro.model';
import { Abast } from '../../core/models/abast.model';
import { Revisao } from '../../core/models/revisao.model';
import { ProximaRevisao } from '../../core/models/proxima.revisao.model';
import { ToastService } from '../../core/services/toast.service';
import { ConfirmationDialogService } from '../../core/services/confirmationDialog.service';
import { AuthService } from '../../core/services/auth.service';
import { CarroStatus } from '../../core/models/carro-status.enum';
import {
  SITUACAO_INFO, SituacaoPreventiva, diasAte, intervaloKm, kmRestantes, nomeVeiculo, progressoRevisaoCarro, situacaoCarro
} from '../../core/utils/situacao.utils';

@Component({
  standalone: false,
  selector: 'app-carro-unico',
  templateUrl: './carro-unico.component.html',
  styleUrls: ['./carro-unico.component.css']
})
export class CarroUnicoComponent implements OnInit {
  carro: Carro | null = null;
  abastecimentos: Abast[] = [];
  revisoes: Revisao[] = [];
  proxima: ProximaRevisao | null = null;
  kmAtual = 0;
  id!: number;
  loading = false;
  activeTab: 'abastecimentos' | 'revisoes' = 'abastecimentos';
  limiteAbast = 10;
  limiteRev = 10;
  expandido: string | null = null;

  isEditing = false;
  edit = { placa: '', alarmeRevisaoKm: null as number | null, alarmeRevisaoMes: null as number | null };

  readonly SIT = SITUACAO_INFO;
  readonly MANUTENCAO = CarroStatus.MANUTENCAO;

  constructor(
    private route: ActivatedRoute,
    private carroService: CarroService,
    private abastService: AbastService,
    private revisaoService: RevisaoService,
    private toastService: ToastService,
    private confirmationDialogService: ConfirmationDialogService,
    private router: Router,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    forkJoin({
      carro: this.carroService.getWithId<Carro>(this.id),
      km: this.carroService.getKmRecente(this.id).pipe(catchError(() => of(0))),
      abast: this.abastService.getListWithId<Abast>(this.id, '/carro').pipe(catchError(() => of([] as Abast[]))),
      rev: this.revisaoService.getListWithId<Revisao>(this.id, '/carro').pipe(catchError(() => of([] as Revisao[]))),
      prox: this.revisaoService.calcularRevisaoById(this.id).pipe(catchError(() => of(null))),
    }).subscribe({
      next: ({ carro, km, abast, rev, prox }) => {
        this.carro = carro;
        this.kmAtual = km || carro.kmAdicionado || 0;
        this.abastecimentos = [...abast].sort((a, b) => String(b.dtAbast).localeCompare(String(a.dtAbast)) || b.kmAtual - a.kmAtual);
        this.revisoes = [...rev].sort((a, b) => String(b.dtRevisao).localeCompare(String(a.dtRevisao)) || b.kmAtual - a.kmAtual);
        this.proxima = prox;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.toastService.showError(err?.status === 404 ? 'Carro não encontrado.' : 'Não foi possível carregar o carro.');
        if (err?.status === 404) this.router.navigate(['/carros']);
      }
    });
  }

  /* ---------- Derivados ---------- */

  get nome(): string { return nomeVeiculo(this.carro?.marca?.marca, this.carro?.modelo?.modelo); }
  get situacao(): SituacaoPreventiva { return situacaoCarro(this.carro, this.proxima, this.kmAtual); }
  get progresso(): number { return progressoRevisaoCarro(this.carro, this.proxima, this.kmAtual); }
  get restante(): number | null { return kmRestantes(this.proxima, this.kmAtual); }
  get dias(): number | null { return diasAte(this.proxima?.proximaRevisaoEstimada); }
  get intervalo(): number { return intervaloKm(this.carro); }
  get mesesIntervalo(): number { return this.carro?.alarmeRevisaoMes && this.carro.alarmeRevisaoMes > 0 ? this.carro.alarmeRevisaoMes : 6; }
  get isAdmin(): boolean { return this.authService.getUserRole(); }
  get emOficina(): boolean { return this.carro?.status === CarroStatus.MANUTENCAO; }

  /** valorComb já é o TOTAL pago no abastecimento. */
  get totalCombustivel(): number {
    return this.abastecimentos.reduce((t, a) => t + (a.valorComb ?? 0), 0);
  }

  get totalManutencao(): number {
    return this.revisoes.reduce((t, r) => t + (r.valor ?? 0), 0);
  }

  precoPorLitro(a: Abast): number | null {
    return a.litroComb > 0 ? a.valorComb / a.litroComb : null;
  }

  /**
   * Média km/L "tanque a tanque": km entre o primeiro e o último abastecimento dividido
   * pelos litros abastecidos DEPOIS do primeiro (o 1º foi gasto antes do período medido).
   */
  get mediaKmPorLitro(): number {
    if (this.abastecimentos.length < 2) return 0;
    const ord = [...this.abastecimentos].sort((a, b) => a.kmAtual - b.kmAtual);
    const km = ord[ord.length - 1].kmAtual - ord[0].kmAtual;
    const litros = ord.slice(1).reduce((t, a) => t + (a.litroComb ?? 0), 0);
    return litros > 0 ? km / litros : 0;
  }

  /** Custo por km rodado: combustível + manutenção. */
  get custoPorKm(): number {
    if (this.abastecimentos.length < 2) return 0;
    const kms = this.abastecimentos.map(a => a.kmAtual);
    const rodado = Math.max(...kms) - Math.min(...kms);
    return rodado > 0 ? (this.totalCombustivel + this.totalManutencao) / rodado : 0;
  }

  get abastVisiveis(): Abast[] { return this.abastecimentos.slice(0, this.limiteAbast); }
  get revVisiveis(): Revisao[] { return this.revisoes.slice(0, this.limiteRev); }

  alternar(chave: string) { this.expandido = this.expandido === chave ? null : chave; }
  abs(n: number) { return Math.abs(n); }

  /* ---------- Ações ---------- */

  confirmDeleteAbastecimento(id: number): void {
    this.confirmationDialogService.confirm('Excluir abastecimento?', 'O registro sai do histórico e dos cálculos de consumo.')
      .subscribe(ok => ok && this.abastService.delete(id).subscribe({
        next: () => {
          this.abastecimentos = this.abastecimentos.filter(a => a.id !== id);
          this.toastService.showSuccess('Abastecimento excluído.');
        },
        error: () => this.toastService.showError('Não foi possível excluir o abastecimento.')
      }));
  }

  confirmDeleteRevisao(id: number): void {
    this.confirmationDialogService.confirm('Excluir revisão?', 'A próxima revisão volta a ser calculada pela revisão anterior.')
      .subscribe(ok => ok && this.revisaoService.delete(id).subscribe({
        next: () => {
          this.toastService.showSuccess('Revisão excluída.');
          this.loadData();
        },
        error: () => this.toastService.showError('Não foi possível excluir a revisão.')
      }));
  }

  deleteCarro() {
    if (!this.isAdmin) return this.toastService.showError('Só administradores podem excluir carros.');
    this.confirmationDialogService.confirm(
      `Excluir ${this.carro?.placa?.toUpperCase() ?? 'o carro'}?`,
      'Todos os abastecimentos e revisões deste carro também serão apagados. Não dá para desfazer.',
      'Excluir carro'
    ).subscribe(ok => {
      if (!ok) return;
      this.loading = true;
      this.carroService.delete(this.id).subscribe({
        next: () => {
          this.loading = false;
          this.toastService.showSuccess('Carro excluído.');
          this.router.navigate(['/carros']);
        },
        error: () => {
          this.loading = false;
          this.toastService.showError('Não foi possível excluir o carro.');
        }
      });
    });
  }

  toggleEditar(): void {
    if (!this.isEditing && this.carro) {
      this.edit = {
        placa: (this.carro.placa ?? '').toUpperCase(),
        alarmeRevisaoKm: this.carro.alarmeRevisaoKm ?? null,
        alarmeRevisaoMes: this.carro.alarmeRevisaoMes ?? null,
      };
    }
    this.isEditing = !this.isEditing;
  }

  sanitizePlaca() {
    this.edit.placa = (this.edit.placa ?? '').replace(/[^a-zA-Z0-9]/g, '').slice(0, 7).toUpperCase();
  }

  salvarAlteracoes(): void {
    if (!this.carro) return;
    if (this.edit.placa.length !== 7) return this.toastService.showWarning('A placa tem 7 caracteres.');
    const dados: Partial<Carro> = {
      placa: this.edit.placa,
      ...(this.edit.alarmeRevisaoKm ? { alarmeRevisaoKm: Number(this.edit.alarmeRevisaoKm) } : {}),
      ...(this.edit.alarmeRevisaoMes ? { alarmeRevisaoMes: Number(this.edit.alarmeRevisaoMes) } : {}),
    };
    this.carroService.put(dados, `/${this.id}`).subscribe({
      next: () => {
        this.toastService.showSuccess('Alterações salvas.');
        this.isEditing = false;
        this.loadData();
      },
      error: (err) => this.toastService.showError(typeof err?.error === 'string' && err.error ? err.error : 'Não foi possível salvar as alterações.')
    });
  }

  alternarStatus() {
    if (!this.carro) return;
    const novo = this.emOficina ? CarroStatus.DISPONIVEL : CarroStatus.MANUTENCAO;
    this.carroService.updateStatus(this.id, novo).subscribe({
      next: () => {
        if (this.carro) this.carro.status = novo;
        this.toastService.showSuccess(novo === CarroStatus.MANUTENCAO ? 'Carro marcado como na oficina.' : 'Carro liberado para uso.');
      },
      error: () => this.toastService.showError('Não foi possível mudar o status.')
    });
  }
}
