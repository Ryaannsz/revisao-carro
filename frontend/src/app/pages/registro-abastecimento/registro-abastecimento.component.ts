import { Component } from '@angular/core';
import { Abast } from '../../core/models/abast.model';
import { AbastService } from '../../core/services/abast.service';

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

  constructor(private abastService: AbastService) {}

  ngOnInit(): void {
    this.abastService.getList<Abast>().subscribe(list => {
      this.abastecimentos = list;
      this.filteredAbastecimentos = list;
    });
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
}
