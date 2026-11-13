import { Component, OnInit } from '@angular/core';
import { forkJoin, of, catchError } from 'rxjs';
import { Carro } from '../../core/models/carro.model';
import { CarroService } from '../../core/services/carro.service';
import { ToastService } from '../../core/services/toast.service';
import { ProximaRevisao } from '../../core/models/proxima.revisao.model';
import { RevisaoService } from '../../core/services/revisao.service';
import { CarroStatus } from '../../core/models/carro-status.enum';
import {
  SITUACAO_INFO, SituacaoPreventiva, diasAte, kmRestantes, textoPrazo, nomeVeiculo, progressoRevisaoCarro, situacaoCarro
} from '../../core/utils/situacao.utils';

type Filtro = 'TODOS' | CarroStatus | 'REVISAO';

interface LinhaCarro {
  carro: Carro;
  nome: string;
  km: number;
  prox?: ProximaRevisao;
  situacao: SituacaoPreventiva;
  progresso: number;
  restante: number | null;
  dias: number | null;
}

const ORDEM: Record<SituacaoPreventiva, number> = { 'vencida': 0, 'proxima': 1, 'ok': 2, 'sem-dados': 3 };

@Component({
  standalone: false,
  selector: 'app-lista-carros',
  templateUrl: './lista-carros.component.html'
})
export class ListaCarrosComponent implements OnInit {
  linhas: LinhaCarro[] = [];
  loading = false;
  erro = false;
  filtro: Filtro = 'TODOS';
  search = '';
  skeletons = Array.from({ length: 5 });

  readonly SIT = SITUACAO_INFO;
  readonly DISPONIVEL = CarroStatus.DISPONIVEL;
  readonly MANUTENCAO = CarroStatus.MANUTENCAO;

  constructor(
    private carroService: CarroService,
    private toastService: ToastService,
    private revisaoService: RevisaoService
  ) { }

  ngOnInit() {
    this.carregar();
  }

  carregar() {
    this.loading = true;
    this.erro = false;
    forkJoin({
      carros: this.carroService.getList<Carro>(),
      proximas: this.revisaoService.calcularRevisao().pipe(catchError(() => of([] as ProximaRevisao[]))),
    }).subscribe({
      next: ({ carros, proximas }) => {
        const porCarro = new Map(proximas.map(p => [p.carroId, p]));
        this.linhas = carros.map(c => {
          const prox = porCarro.get(c.id);
          const km = prox?.kmAtual ?? c.kmAdicionado ?? 0;
          return {
            carro: c,
            nome: nomeVeiculo(c.marca?.marca, c.modelo?.modelo),
            km,
            prox,
            situacao: situacaoCarro(c, prox, km),
            progresso: progressoRevisaoCarro(c, prox, km),
            restante: kmRestantes(prox, km),
            dias: diasAte(prox?.proximaRevisaoEstimada),
          };
        }).sort((a, b) => ORDEM[a.situacao] - ORDEM[b.situacao] || a.carro.placa.localeCompare(b.carro.placa));
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.erro = true;
        this.toastService.showError('Não foi possível carregar os carros.');
      }
    });
  }

  private precisaRevisao(l: LinhaCarro) {
    return l.situacao === 'vencida' || l.situacao === 'proxima';
  }

  get filtradas(): LinhaCarro[] {
    const q = this.search.trim().toLowerCase();
    return this.linhas.filter(l => {
      const passaFiltro =
        this.filtro === 'TODOS' ? true :
        this.filtro === 'REVISAO' ? this.precisaRevisao(l) :
        l.carro.status === this.filtro;
      const passaBusca = !q || l.nome.toLowerCase().includes(q) || (l.carro.placa ?? '').toLowerCase().includes(q);
      return passaFiltro && passaBusca;
    });
  }

  contar(f: Filtro): number {
    if (f === 'TODOS') return this.linhas.length;
    if (f === 'REVISAO') return this.linhas.filter(l => this.precisaRevisao(l)).length;
    return this.linhas.filter(l => l.carro.status === f).length;
  }

  limparFiltros() {
    this.search = '';
    this.filtro = 'TODOS';
  }

  trackBy(_: number, l: LinhaCarro) {
    return l.carro.id;
  }

  abs(n: number) { return Math.abs(n); }
  prazo(l: { restante: number | null; dias: number | null }) { return textoPrazo(l.restante, l.dias, 'km', 'Revisão'); }
}
