import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CarroService } from '../../core/services/carro.service';
import { AbastService } from '../../core/services/abast.service';
import { RevisaoService } from '../../core/services/revisao.service';
import { Carro } from '../../core/models/carro.model';
import { Abast } from '../../core/models/abast.model';
import { Revisao } from '../../core/models/revisao.model';
import { ToastService } from '../../core/services/toast.service';
import { ConfirmationDialogService } from '../../core/services/confirmationDialog.service';
import { AuthService } from '../../core/services/auth.service';

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
  expAbastId: number | null = null;
  expRevisaoId: number | null = null;
  id!: number;
  loading: boolean = false;
  activeTab: 'abastecimentos' | 'revisoes' = 'abastecimentos';
  currentPageAbast: number = 1;
  currentPageRevisoes: number = 1;
  itemsPerPage: number = 10;
  isEditing: boolean = false;
  carroEditado: Partial<Carro> = {};



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
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.id = Number(this.route.snapshot.paramMap.get('id'));

    this.carroService.getWithId<Carro>(this.id).subscribe({
      next: (c) => {
        this.carro = c;
        this.getKm();
      },
      error: (err) => {
        this.toastService.showError('Erro ao carregar dados do veículo');
        this.loading = false;
      }
    });

    this.abastService.getListWithId<Abast>(this.id, '/carro').subscribe({
      next: (a) => this.abastecimentos = a,
      error: (err) => this.toastService.showError('Erro ao carregar abastecimentos')
    });

    this.revisaoService.getListWithId<Revisao>(this.id, '/carro').subscribe({
      next: (r) => {
        this.revisoes = r;
        this.loading = false;
      },
      error: (err) => {
        this.toastService.showError('Erro ao carregar revisões');
        this.loading = false;
      }
    });
  }

  toggleAbast(id: number): void {
    this.expAbastId = this.expAbastId === id ? null : id;
  }

  toggleRevisao(id: number): void {
    this.expRevisaoId = this.expRevisaoId === id ? null : id;
  }

  getKm(): void {
    this.carroService.getKmRecente(this.carro?.idCarro ?? 0).subscribe({
      next: (res) => {
        if (this.carro) {
          this.carro.kmAdicionado = res;
        }
      },
      error: (err) => {
        this.toastService.showError('Erro ao carregar quilometragem atual');
      }
    });
  }

  // Novas funcionalidades

  changeTab(tab: 'abastecimentos' | 'revisoes'): void {
    this.activeTab = tab;
  }

  changePageAbast(page: number): void {
    this.currentPageAbast = page;
  }

  changePageRevisoes(page: number): void {
    this.currentPageRevisoes = page;
  }

  get paginatedAbastecimentos(): Abast[] {
    const start = (this.currentPageAbast - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    return this.abastecimentos.slice(start, end);
  }

  get paginatedRevisoes(): Revisao[] {
    const start = (this.currentPageRevisoes - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    return this.revisoes.slice(start, end);
  }

  confirmDeleteAbastecimento(id: number): void {
    this.confirmationDialogService.confirm(
      'Confirmar exclusão',
      'Tem certeza que deseja excluir este abastecimento?'
    ).subscribe(confirmed => {
      if (confirmed) {
        this.deleteAbastecimento(id);
      }
    });
  }

  confirmDeleteRevisao(id: number): void {
    this.confirmationDialogService.confirm(
      'Confirmar exclusão',
      'Tem certeza que deseja excluir esta revisão?'
    ).subscribe(confirmed => {
      if (confirmed) {
        this.deleteRevisao(id);
      }
    });
  }

  deleteCarro() {
    if (!this.authService.getUserRole()) return this.toastService.showError("Permissões insuficientes.")
    this.confirmationDialogService.confirm(
      'Confirmar exclusão',
      'Tem certeza que deseja excluir este carro?'
    ).subscribe(confirmed => {
      if (!confirmed) return;
      this.loading = true;
      this.carroService.delete(this.id.toString()).subscribe({
        next: (res) => {
          this.toastService.showSuccess("Carro deletado com sucesso!");
          this.loading = false;
          this.router.navigate(['/carros'])
        },
        error: (err) => {
          this.toastService.showError("Erro ao deletar o carro!");
          this.loading = false;
          console.log(err)
        }
      })
    })
    this.loading = false;
  }

  private deleteAbastecimento(id: number): void {

  }

  private deleteRevisao(id: number): void {

  }

  get totalPagesAbast(): number {
    return Math.ceil(this.abastecimentos.length / this.itemsPerPage);
  }

  get totalPagesRevisoes(): number {
    return Math.ceil(this.revisoes.length / this.itemsPerPage);
  }

  // Métodos para calcular totais (opcional)
  get totalGastoAbastecimentos(): number {
    return this.abastecimentos.reduce((total, a) => total + (a.litroComb * a.valorComb), 0);
  }

  get mediaKmPorLitro(): number {
    if (this.abastecimentos.length < 2) return 0;
    // Cálculo simplificado - implemente sua lógica real aqui
    return 10; // Exemplo
  }

  toggleEditar(): void {
    if (!this.isEditing && this.carro) {
      // Criar uma cópia editável
      this.carroEditado = {
        ...this.carro,
        marca: { ...this.carro.marca },
        modelo: { ...this.carro.modelo }
      };
    }
    this.isEditing = !this.isEditing;
  }

  salvarAlteracoes(): void {
    if (!this.carro) return;

    const carroAtualizado: Carro = {
      ...this.carro,
      ...this.carroEditado
    };
    this.carroService.put(carroAtualizado, `/${this.id}`).subscribe({
      next: (res) => {
        this.toastService.showSuccess('Carro atualizado com sucesso!');
        this.loadData();
        this.isEditing = false;
      },
      error: (err) => {
        this.toastService.showError('Erro ao atualizar o carro')
      }

    });
  }
}