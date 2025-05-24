import { Component } from '@angular/core';
import { Revisao } from '../../core/models/revisao.model';
import { RevisaoService } from '../../core/services/revisao.service';
import { ToastService } from '../../core/services/toast.service';
import { ConfirmationDialogService } from '../../core/services/confirmationDialog.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-registro-revisao',
  standalone: false,
  templateUrl: './registro-revisao.component.html',
  styleUrl: './registro-revisao.component.css'
})
export class RegistroRevisaoComponent {
  revisoes: Revisao[] = [];
  filteredRevisoes: Revisao[] = [];
  searchQuery: string = '';
  selectedRevisao: Revisao | null = null;
  loading: boolean = false;

  constructor(private revisaoService: RevisaoService,
    private toastService: ToastService,
    private confirmationService: ConfirmationDialogService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.revisaoService.getList<Revisao>().subscribe(list => {
      this.revisoes = list;
      this.filteredRevisoes = list;
    });
  }

  onSearch(): void {
    const q = this.searchQuery.trim().toLowerCase();
    if (!q) {
      this.filteredRevisoes = [...this.revisoes];
      return;
    }
    this.filteredRevisoes = this.revisoes.filter(r =>
      r.carro.marca.marca.toLowerCase().includes(q) ||
      r.carro.modelo.modelo.toLowerCase().includes(q) ||
      r.carro.placa.toLowerCase().includes(q)
    );
  }

  openModal(r: Revisao): void {
    this.selectedRevisao = r;
  }

  closeModal(): void {
    this.selectedRevisao = null;
  }

  removeRevisao(id: number): void {
    if (!this.authService.getUserRole()) return this.toastService.showError("Permissões insuficientes.")
    this.confirmationService.confirm(
      'Tem certeza?',
      'Você deseja realmente excluir este item?'
    ).subscribe(result => {
      this.loading = true;
      if (!result) return;
      this.revisaoService.delete(`/${id}`).subscribe(() => {
        this.loading = false;
        this.revisoes = this.revisoes.filter(r => r.idRevisao !== id);
        this.onSearch();
        this.closeModal();
        this.toastService.showSuccess("Revisão removida com sucesso!")
        this.loading = false;
      });
    })
  }

  editRevisao(id: number): void {

  }
}