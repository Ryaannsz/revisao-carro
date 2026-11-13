import { Component, OnInit } from '@angular/core';
import { Abast } from '../../core/models/abast.model';
import { AbastService } from '../../core/services/abast.service';
import { ToastService } from '../../core/services/toast.service';
import { ConfirmationDialogService } from '../../core/services/confirmationDialog.service';
import { AuthService } from '../../core/services/auth.service';
import { GrupoMes, agruparPorMes } from '../../core/utils/agrupar.utils';
import { nomeVeiculo } from '../../core/utils/situacao.utils';

@Component({
  selector: 'app-registro-abastecimento',
  standalone: false,
  templateUrl: './registro-abastecimento.component.html',
  styleUrl: './registro-abastecimento.component.css'
})
export class RegistroAbastecimentoComponent implements OnInit {
  abastecimentos: Abast[] = [];
  searchQuery = '';
  loading = false;
  limite = 40;
  aberto: number | null = null;

  constructor(private abastService: AbastService,
    private toastService: ToastService,
    private confirmationService: ConfirmationDialogService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.carregarAbast();
  }

  get isAdmin() { return this.authService.getUserRole(); }

  nome(a: Abast) { return nomeVeiculo(a.carro?.marca?.marca, a.carro?.modelo?.modelo); }
  precoLitro(a: Abast) { return a.litroComb > 0 ? a.valorComb / a.litroComb : null; }

  get filtrados(): Abast[] {
    const q = this.searchQuery.trim().toLowerCase();
    if (!q) return this.abastecimentos;
    return this.abastecimentos.filter(a =>
      this.nome(a).toLowerCase().includes(q) || (a.carro?.placa ?? '').toLowerCase().includes(q));
  }

  get grupos(): GrupoMes<Abast>[] {
    return agruparPorMes(this.filtrados.slice(0, this.limite), a => a.dtAbast as any, a => a.valorComb);
  }

  get totalGasto() { return this.filtrados.reduce((t, a) => t + (a.valorComb ?? 0), 0); }
  get totalLitros() { return this.filtrados.reduce((t, a) => t + (a.litroComb ?? 0), 0); }

  removeAbast(a: Abast): void {
    this.confirmationService.confirm('Excluir abastecimento?', `Abastecimento de ${a.carro?.placa?.toUpperCase()} em ${new Date(String(a.dtAbast).slice(0, 10) + 'T00:00').toLocaleDateString('pt-BR')}.`)
      .subscribe(ok => {
        if (!ok) return;
        this.loading = true;
        this.abastService.delete(a.id).subscribe({
          next: () => {
            this.abastecimentos = this.abastecimentos.filter(x => x.id !== a.id);
            this.loading = false;
            this.toastService.showSuccess('Abastecimento excluído.');
          },
          error: () => {
            this.loading = false;
            this.toastService.showError('Não foi possível excluir o abastecimento.');
          }
        });
      });
  }

  carregarAbast() {
    this.loading = true;
    this.abastService.getList<Abast>().subscribe({
      next: (list) => {
        this.loading = false;
        this.abastecimentos = [...list].sort((a, b) => String(b.dtAbast).localeCompare(String(a.dtAbast)) || b.id - a.id);
      },
      error: () => {
        this.loading = false;
        this.toastService.showError('Não foi possível carregar os abastecimentos.');
      }
    });
  }
}
