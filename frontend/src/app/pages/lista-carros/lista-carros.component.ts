import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Carro } from '../../core/models/carro.model';
import { CarroService } from '../../core/services/carro.service';
import { Router } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { ToastService } from '../../core/services/toast.service';
import { ProximaRevisao } from '../../core/models/proxima.revisao.model';
import { RevisaoService } from '../../core/services/revisao.service';
import { CarroStatus } from '../../core/models/carro-status.enum';

@Component({
  standalone: false,
  selector: 'app-lista-carros',
  templateUrl: './lista-carros.component.html'
})
export class ListaCarrosComponent implements OnInit {
  carros: Carro[] = [];
  loading: boolean = false;
  revisoes: ProximaRevisao[] = [];

  statusFilter: CarroStatus | 'TODOS' = 'TODOS';

  carroDisponivel: CarroStatus= CarroStatus.DISPONIVEL;

  carroManutencao: CarroStatus= CarroStatus.MANUTENCAO



  search = '';
  skeletons = Array.from({ length: 6 });

  constructor(
    private carroService: CarroService,
    private router: Router,
    private toastService: ToastService,
    private revisaoService: RevisaoService
  ) { }

  ngOnInit() {
    this.carregarCarros();
    this.calcularRevisao();
  }

  carregarCarros() {
    this.loading = true;
    this.carroService.getList<Carro>().subscribe({
      next: (data) => {
        this.carros = data;
        this.getKm();
        this.loading = false;
        this.toastService.showSuccess('Carros listados com sucesso!');
      },
      error: (err) => {
        console.error('Erro ao carregar carros:', err);
        this.loading = false;
        this.toastService.showError('Erro ao listar carros!');
      }
    });
  }

  getKm() {
    this.carros.map((car) => {
      this.carroService.getKmRecente(car.id).subscribe({
        next: (res) => { car.kmAdicionado = res; }
      });
    });
  }

  irParaCarro(id: number) {
    this.router.navigate(['/carros/unico', id]);
  }

get carrosFiltrados(): Carro[] {
  const q = (this.search ?? '').trim().toLowerCase();

  return (this.carros ?? []).filter(carro => {

    const matchStatus =
      this.statusFilter === 'TODOS' ? true : carro.status === this.statusFilter;


    const matchSearch =
      !q ||
      (carro.marca?.marca ?? '').toLowerCase().includes(q) ||
      (carro.modelo?.modelo ?? '').toLowerCase().includes(q) ||
      (carro.placa ?? '').toLowerCase().includes(q);

    return matchStatus && matchSearch;
  });
  }

  // NOVO
  clearSearch() {
    this.search = '';
  }

  // NOVO
  trackByCarro(index: number, item: Carro) {
    return item.id ?? index;
  }

  calcularRevisao() {
    this.revisaoService.calcularRevisao().subscribe((res) => {
      this.revisoes = res;
    })
  }

  getProximaRevisaoByCarroId(carroId: number): ProximaRevisao | undefined {
    return this.revisoes.find(r => r.carroId === carroId);
  }
  getProgressPercent(carro: Carro): number {
    const atual = carro?.kmAdicionado ?? 0;

    // alvo absoluto (odômetro) para a próxima revisão
    const r = this.getProximaRevisaoByCarroId(carro.id);
    if (!r || r.kmRevisaoEstimado == null) return 0;

    const alvo = Number(r.kmRevisaoEstimado);

    // se tiver intervalo configurado no carro, calcule progresso dentro do ciclo
    const intervalo = carro?.alarmeRevisaoKm ?? 0;

    if (intervalo > 0) {
      const inicioCiclo = alvo - intervalo;               // odômetro na revisão anterior
      const progressoKm = atual - inicioCiclo;            // km percorridos no ciclo atual
      const percent = (progressoKm / intervalo) * 100;
      return Math.max(0, Math.min(100, percent));
    }

    // Fallback (sem intervalo): quanto falta até o alvo absoluto
    // Se já passou do alvo, 100%
    if (alvo <= 0) return 0;
    const percentAbs = (atual / alvo) * 100;
    return Math.max(0, Math.min(100, percentAbs));
  }

  updateStatusFilter(status: CarroStatus | 'TODOS'){
     this.statusFilter = status;
  }
}
