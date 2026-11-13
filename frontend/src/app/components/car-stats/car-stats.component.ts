import { Component, OnInit } from '@angular/core';
import { DashboardService } from '../../core/services/dashboard.service';
import { CarOption } from '../../core/models/car.option.model';
import { CarStatsResponse } from '../../core/models/car.stats.model';
import { MesValor } from '../../core/models/mes.valor.model';
import { AppConstants } from '../../core/constants/app.constants';
import { PdfService } from '../../core/services/pdf.service';
import { ToastService } from '../../core/services/toast.service';
import { SERIES } from '../line-chart/line-chart.component';

/** Máximo de carros no modo comparar: uma cor da paleta validada para cada. */
const MAX_COMPARAR = SERIES.length;

@Component({
  selector: 'app-car-stats',
  templateUrl: './car-stats.component.html',
  standalone: false
})
export class CarStatsComponent implements OnInit {
  allCars: CarOption[] = [];
  selectedIds: number[] = [];
  months = 6;                     // 0 = tudo
  mode: 'sum' | 'compare' = 'compare';
  loading = false;
  gerandoPdf = false;
  readonly max = MAX_COMPARAR;
  readonly periodos = [{ v: 3, l: '3m' }, { v: 6, l: '6m' }, { v: 12, l: '12m' }, { v: 0, l: 'Tudo' }];

  /** A cor segue o carro enquanto ele estiver selecionado (não muda quando outro sai). */
  private corPorId = new Map<number, string>();

  readonly opcoesMoeda = {
    plugins: { tooltip: { callbacks: { label: (c: any) => `${c.dataset.label}: ${c.parsed.y == null ? 'sem dado' : Number(c.parsed.y).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}` } } },
    scales: { y: { ticks: { callback: (v: number) => Number(v).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL', maximumFractionDigits: 0 }) } } },
  };
  readonly opcoesNumero = {
    plugins: { tooltip: { callbacks: { label: (c: any) => `${c.dataset.label}: ${c.parsed.y == null ? 'sem dado' : Number(c.parsed.y).toLocaleString('pt-BR', { maximumFractionDigits: 1 })}` } } },
  };

  vm = { labels: [] as string[], gasto: [] as any[], litros: [] as any[], eficiencia: [] as any[] };

  constructor(private svc: DashboardService,
    private pdfService: PdfService,
    private toastService: ToastService
  ) { }

  ngOnInit(): void {
    this.svc.getCarOption().subscribe({
      next: (cars) => this.allCars = [...cars].sort((a, b) => a.label.localeCompare(b.label)),
      error: () => this.toastService.showError('Não foi possível carregar a lista de carros.')
    });
  }

  isSel(id: number) { return this.selectedIds.includes(id); }
  cor(id: number) { return this.corPorId.get(id) ?? null; }

  get limiteAtingido() { return this.mode === 'compare' && this.selectedIds.length >= MAX_COMPARAR; }

  toggle(id: number) {
    if (this.isSel(id)) {
      this.selectedIds = this.selectedIds.filter(x => x !== id);
      this.corPorId.delete(id);
    } else {
      if (this.limiteAtingido) {
        this.toastService.showInfo(`Para comparar, escolha até ${MAX_COMPARAR} carros. Para mais, use “Somar”.`);
        return;
      }
      this.selectedIds = [...this.selectedIds, id];
      this.atribuirCor(id);
    }
    this.load();
  }

  private atribuirCor(id: number) {
    const usadas = new Set(this.corPorId.values());
    const livre = SERIES.find(c => !usadas.has(c));
    if (livre) this.corPorId.set(id, livre);
  }

  selectAll() {
    const ids = this.allCars.map(c => c.id);
    if (this.mode === 'compare' && ids.length > MAX_COMPARAR) {
      this.mode = 'sum';
      this.toastService.showInfo(`Com mais de ${MAX_COMPARAR} carros o gráfico mostra a soma.`);
    }
    this.selectedIds = ids;
    ids.forEach(id => this.corPorId.has(id) || this.atribuirCor(id));
    this.load();
  }

  clearAll() {
    this.selectedIds = [];
    this.corPorId.clear();
    this.load();
  }

  setMode(m: 'sum' | 'compare') {
    if (m === 'compare' && this.selectedIds.length > MAX_COMPARAR) {
      this.toastService.showInfo(`Para comparar, deixe no máximo ${MAX_COMPARAR} carros selecionados.`);
      return;
    }
    this.mode = m;
    this.load();
  }

  setMonths(m: number) { this.months = m; this.load(); }
  labelDoCarro(id: number) { return this.allCars.find(c => c.id === id)?.label?.toUpperCase() ?? `#${id}`; }

  load(): void {
    if (!this.selectedIds.length) {
      this.vm = { labels: [], gasto: [], litros: [], eficiencia: [] };
      return;
    }
    this.loading = true;
    this.svc.getCarStats(this.selectedIds, this.months, this.mode).subscribe({
      next: (res: CarStatsResponse) => {
        const labels = res.labels.map(m => this.formatMes(m));
        const soma = this.mode === 'sum';
        this.vm = {
          labels,
          gasto: soma ? [this.ds('Gasto', SERIES[0], res.gasto.sum)] : this.compare(res.gasto.byCar),
          litros: soma ? [this.ds('Litros', SERIES[0], res.litros.sum)] : this.compare(res.litros.byCar),
          eficiencia: soma ? [this.ds('km/L', SERIES[0], res.eficiencia.sum)] : this.compare(res.eficiencia.byCar),
        };
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.toastService.showError('Não foi possível carregar a comparação.');
      }
    });
  }

  generateReport() {
    if (!this.selectedIds.length) {
      this.toastService.showInfo('Escolha pelo menos um carro para o relatório.');
      return;
    }
    this.gerandoPdf = true;
    this.pdfService.makeCarOptionReport(this.selectedIds, this.months, this.mode).subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'relatorio-frota.pdf';
        a.click();
        window.URL.revokeObjectURL(url);
        this.gerandoPdf = false;
        this.toastService.showSuccess('Relatório baixado.');
      },
      error: () => {
        this.gerandoPdf = false;
        this.toastService.showError('Não foi possível gerar o relatório.');
      }
    });
  }

  private ds(label: string, color: string, arr: MesValor[]) {
    return { label, borderColor: color, data: arr.map(p => p.valor) };
  }

  private compare(byCar: Record<number, MesValor[]>) {
    return this.selectedIds
      .filter(id => byCar[id])
      .map(id => this.ds(this.labelDoCarro(id), this.cor(id) ?? SERIES[0], byCar[id] ?? []));
  }

  private formatMes(m: string): string {
    const [ano, mes] = m.split('-').map(Number);
    if (!ano || !mes) return m;
    return `${AppConstants.MESES_ABREV[mes - 1]}/${String(ano).slice(-2)}`;
  }
}
