import { Component } from '@angular/core';
import { Abast } from '../../core/models/abast.model';
import { AbastService } from '../../core/services/abast.service';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-registro-abastecimento',
  standalone: false,
  templateUrl: './registro-abastecimento.component.html',
  styleUrl: './registro-abastecimento.component.css'
})
export class RegistroAbastecimentoComponent {

  abastecimentos: Abast[] = [];
  filteredAbastecimentos: Abast[] = [];
  searchQuery: string = '';
  selectedAbast: Abast | null = null;
  loading: boolean = false;

  constructor(private abastService: AbastService,
    private toastService: ToastService
  ) { }

  ngOnInit(): void {
    this.carregarAbast();
  }

  onSearch(): void {
    const q = this.searchQuery.trim().toLowerCase();
    if (!q) {
      this.filteredAbastecimentos = [...this.abastecimentos];
      return;
    }
    this.filteredAbastecimentos = this.abastecimentos.filter(a =>
      a.carro.marca.marca.toLowerCase().includes(q) ||
      a.carro.modelo.modelo.toLowerCase().includes(q) ||
      a.carro.placa.toLowerCase().includes(q)
    );
  }

  openModal(abast: Abast): void {
    this.selectedAbast = abast;
  }

  closeModal(): void {
    this.selectedAbast = null;
  }

  removeAbast(id: number): void {
    this.abastService.delete(`/${id}`).subscribe(() => {
      this.abastecimentos = this.abastecimentos.filter(a => a.idAbast !== id);
      this.onSearch();
      this.closeModal();
    });
  }

  editAbast(id: number): void {

  }

  carregarAbast() {
    this.loading = true;
    this.abastService.getList<Abast>().subscribe({
      next: (list) => {
        this.loading = false;
        this.abastecimentos = list;
        this.filteredAbastecimentos = list;
        if (list.length === 0)
          this.toastService.showInfo("Nenhum abastecimento registrado.")
        else
          this.toastService.showSuccess("Abastecimentos carregados!")
      },
      error: (err) => {
        this.loading = false;
        this.toastService.showError('Erro ao buscar lista!');
      }
    });

  }
}
