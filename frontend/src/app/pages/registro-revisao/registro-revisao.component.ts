import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { catchError, of } from 'rxjs';
import { Revisao } from '../../core/models/revisao.model';
import { RevisaoMaquina } from '../../core/models/revisao.maquina.model';
import { RevisaoService } from '../../core/services/revisao.service';
import { RevisaoMaquinaService } from '../../core/services/maquina.revisao.service';
import { ToastService } from '../../core/services/toast.service';
import { ConfirmationDialogService } from '../../core/services/confirmationDialog.service';
import { AuthService } from '../../core/services/auth.service';
import { GrupoMes, agruparPorMes } from '../../core/utils/agrupar.utils';
import { nomeVeiculo } from '../../core/utils/situacao.utils';
import { nomeMaquina } from '../../core/utils/maquina.utils';

/** Linha comum para revisão de carro e manutenção de máquina. */
interface LinhaRevisao {
  id: number;
  tipo: 'carro' | 'maquina';
  data: string;
  titulo: string;
  placa: string | null;
  medidor: number;
  unidade: 'km' | 'h';
  valor: number | null;
  descricao: string | null;
  usuario: string | null;
  link: any[];
}

@Component({
  selector: 'app-registro-revisao',
  standalone: false,
  templateUrl: './registro-revisao.component.html',
  styleUrl: './registro-revisao.component.css'
})
export class RegistroRevisaoComponent implements OnInit {
  carros: LinhaRevisao[] = [];
  maquinas: LinhaRevisao[] = [];
  aba: 'carro' | 'maquina' = 'carro';
  searchQuery = '';
  loading = false;
  limite = 40;

  constructor(private revisaoService: RevisaoService,
    private revisaoMaquinaService: RevisaoMaquinaService,
    private toastService: ToastService,
    private confirmationService: ConfirmationDialogService,
    private authService: AuthService,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    if (this.route.snapshot.queryParamMap.get('tipo') === 'maquina') this.aba = 'maquina';
    this.loading = true;
    let pendentes = 2;
    const fim = () => { if (--pendentes === 0) this.loading = false; };

    this.revisaoService.getList<Revisao>().pipe(catchError(() => {
      this.toastService.showError('Não foi possível carregar as revisões de carros.');
      return of([] as Revisao[]);
    })).subscribe(list => {
      this.carros = list.map(r => ({
        id: r.id, tipo: 'carro' as const, data: String(r.dtRevisao), titulo: nomeVeiculo(r.carro?.marca?.marca, r.carro?.modelo?.modelo),
        placa: r.carro?.placa ?? null, medidor: r.kmAtual, unidade: 'km' as const, valor: r.valor ?? null,
        descricao: r.descricao ?? null, usuario: r.user?.nome ?? null, link: ['/carros/unico', r.carro?.id],
      })).sort((a, b) => b.data.localeCompare(a.data) || b.id - a.id);
      fim();
    });

    this.revisaoMaquinaService.listar().pipe(catchError(() => of([] as RevisaoMaquina[]))).subscribe(list => {
      this.maquinas = list.map(r => ({
        id: r.id, tipo: 'maquina' as const, data: String(r.dtRevisao), titulo: nomeMaquina(r.maquina),
        placa: null, medidor: r.horasAtual, unidade: 'h' as const, valor: r.valor ?? null,
        descricao: r.descricao ?? null, usuario: r.user?.nome ?? null, link: ['/maquinas', r.maquina?.id],
      })).sort((a, b) => b.data.localeCompare(a.data) || b.id - a.id);
      fim();
    });
  }

  get isAdmin() { return this.authService.getUserRole(); }

  get lista(): LinhaRevisao[] { return this.aba === 'carro' ? this.carros : this.maquinas; }

  get filtradas(): LinhaRevisao[] {
    const q = this.searchQuery.trim().toLowerCase();
    if (!q) return this.lista;
    return this.lista.filter(r => [r.titulo, r.placa, r.descricao].some(v => (v ?? '').toLowerCase().includes(q)));
  }

  get grupos(): GrupoMes<LinhaRevisao>[] {
    return agruparPorMes(this.filtradas.slice(0, this.limite), r => r.data, r => r.valor ?? 0);
  }

  get totalGasto() { return this.filtradas.reduce((t, r) => t + (r.valor ?? 0), 0); }

  trocarAba(a: 'carro' | 'maquina') {
    this.aba = a;
    this.limite = 40;
  }

  remover(r: LinhaRevisao): void {
    const titulo = r.tipo === 'carro' ? 'Excluir revisão?' : 'Excluir manutenção?';
    this.confirmationService.confirm(titulo, `${r.placa?.toUpperCase() ?? r.titulo}: a próxima revisão volta a ser calculada pelo registro anterior.`)
      .subscribe(ok => {
        if (!ok) return;
        const svc = r.tipo === 'carro' ? this.revisaoService : this.revisaoMaquinaService;
        this.loading = true;
        svc.delete(r.id).subscribe({
          next: () => {
            this.loading = false;
            if (r.tipo === 'carro') this.carros = this.carros.filter(x => x.id !== r.id);
            else this.maquinas = this.maquinas.filter(x => x.id !== r.id);
            this.toastService.showSuccess(r.tipo === 'carro' ? 'Revisão excluída.' : 'Manutenção excluída.');
          },
          error: () => {
            this.loading = false;
            this.toastService.showError('Não foi possível excluir.');
          }
        });
      });
  }
}
