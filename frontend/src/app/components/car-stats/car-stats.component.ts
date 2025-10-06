// src/app/pages/dashboard/car-stats/car-stats.component.ts
import { Component, OnInit } from '@angular/core';
import { DashboardService } from '../../core/services/dashboard.service';
import { CarOption } from '../../core/models/car.option.model';
import { CarStatsResponse } from '../../core/models/car.stats.model';
import { MesValor } from '../../core/models/mes.valor.model';
import { AppConstants } from '../../core/constants/app.constants';
import { PdfService } from '../../core/services/pdf.service';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-car-stats',
  templateUrl: './car-stats.component.html',
  standalone: false
})
export class CarStatsComponent implements OnInit {
  allCars: CarOption[] = [];      // opções disponíveis (retornadas pelo endpoint conforme carIds)
  selectedIds: number[] = [];     // seleção atual
  months = 6;                     // 0 = tudo
  mode: 'sum' | 'compare' = 'compare';
  loading: boolean = false;

  // paleta de cores para comparar vários carros
  private palette = ['#2563eb', '#059669', '#9333ea', '#f59e0b', '#ef4444', '#10b981', '#8b5cf6', '#f97316', '#14b8a6'];

  // chart options
  chartOptionsCurrency = this.makeChartOptions('R$');
  chartOptionsNumber = this.makeChartOptions();

  // view model para os 3 gráficos
  vm = { labels: [] as string[], gasto: [] as any[], litros: [] as any[], eficiencia: [] as any[] };

  constructor(private svc: DashboardService,
    private pdfService: PdfService,
    private toastService: ToastService
  ) { }

  ngOnInit(): void {
    this.svc.getCarOption().subscribe((car) => {
      this.allCars = car
    });
    this.selectedIds = this.selectedIds.length ? this.selectedIds : [];
    this.load();
  }

  load(): void {
    if (!this.selectedIds.length) {
      this.vm = { labels: [], gasto: [], litros: [], eficiencia: [] };
      return;
    }
    this.loading=true;
    this.svc.getCarStats(this.selectedIds, this.months, this.mode).subscribe((res: CarStatsResponse) => {
      // options do select (cars com kmAtual)

      // labels formatadas para Jan/25
      const labelsFmt = res.labels.map(this.formatMes);

      // monta datasets de acordo com o modo
      const gastoDS = (this.mode === 'sum')
        ? [this.makeDS('Gasto', this.palette[0], res.gasto.sum)]
        : this.makeCompareDatasets(res.gasto.byCar);
      const litrosDS = (this.mode === 'sum')
        ? [this.makeDS('Litros', this.palette[1], res.litros.sum)]
        : this.makeCompareDatasets(res.litros.byCar, 1);
      const eficDS = (this.mode === 'sum')
        ? [this.makeDS('km/L', this.palette[2], res.eficiencia.sum)]
        : this.makeCompareDatasets(res.eficiencia.byCar, 2);

      this.vm = { labels: labelsFmt, gasto: gastoDS, litros: litrosDS, eficiencia: eficDS };
    });
    this.loading=false;
  }

  generateReport() {
    if (!this.selectedIds.length) {
      this.toastService.showInfo('Você precisa selecionar ao menos um.')
      return;
    }
    this.loading=true;
    this.pdfService.makeCarOptionReport(this.selectedIds, this.months, this.mode).subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'relatorio-dashboard.pdf';
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => this.toastService.showError('Erro ao gerar relatório', err)
    });
    this.loading=false;
    this.toastService.showSuccess('Relatório gerado com sucesos!')
  }



  toggleCarEvt(id: number, ev: Event) {
    const input = ev.target as HTMLInputElement | null;
    const checked = !!input?.checked; // evita "possibly null"
    if (checked) {
      if (!this.selectedIds.includes(id)) this.selectedIds = [...this.selectedIds, id];
    } else {
      this.selectedIds = this.selectedIds.filter(x => x !== id);
    }
    this.onSelectChange(this.selectedIds);
  }

  // ================= UI handlers =================

  selectAll() { this.selectedIds = this.allCars.map(c => c.id); this.onSelectChange(this.selectedIds); }
  clearAll() { this.selectedIds = []; this.onSelectChange(this.selectedIds); }
  onSelectChange(ids: number[]) { this.selectedIds = ids; }
  setMode(m: 'sum' | 'compare') { this.mode = m; this.load(); }
  setMonths(m: number) { this.months = m; this.load(); }
  remove(id: number) { this.selectedIds = this.selectedIds.filter(x => x !== id); this.load(); }
  labelDoCarro(id: number) { return this.allCars.find(c => c.id === id)?.label ?? `#${id}`; }

  // ================= helpers =================
  private makeDS(label: string, color: string, arr: MesValor[]) {
    const data = arr.map(p => p.valor); // aceita null => gaps
    return {
      label,
      data,
      borderColor: color,
      backgroundColor: this.hexToRgba(color, 0.10),
      tension: 0.3,
      pointRadius: 3,
      fill: true,
      spanGaps: false
    };
  }

  private makeCompareDatasets(byCar: Record<number, MesValor[]>, colorOffset = 0) {
    const ids = Object.keys(byCar).map(Number).sort((a, b) => a - b);
    return ids.map((id, idx) => {
      const color = this.palette[(idx + colorOffset) % this.palette.length];
      return this.makeDS(`${this.labelDoCarro(id)}`, color, byCar[id] ?? []);
    });
  }

  private makeChartOptions(currencySymbol?: string): any {
    return {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: true },
        tooltip: {
          callbacks: {
            label: (ctx: any) => {
              const v = ctx.parsed?.y;
              if (v == null) return `${ctx.dataset.label}: s/dado`;
              if (currencySymbol) {
                return `${ctx.dataset.label}: ${currencySymbol} ${v.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
              }
              return `${ctx.dataset.label}: ${Number(v).toLocaleString('pt-BR')}`;
            }
          }
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            callback: (value: number) => currencySymbol
              ? `${currencySymbol} ${Number(value).toLocaleString('pt-BR')}`
              : Number(value).toLocaleString('pt-BR')
          }
        }
      }
    };
  }

  private formatMes(m: string): string {
    // m = "YYYY-MM"
    const mesesAbrev = AppConstants.MESES_ABREV;
    const [ano, mes] = m.split('-').map(Number);
    if (!ano || !mes) return m;
    return `${mesesAbrev[mes - 1]}/${String(ano).slice(-2)}`;
  }

  private hexToRgba(hex: string, alpha = 0.15) {
    const h = hex.replace('#', '');
    const r = parseInt(h.substring(0, 2), 16);
    const g = parseInt(h.substring(2, 4), 16);
    const b = parseInt(h.substring(4, 6), 16);
    return `rgba(${r}, ${g}, ${b}, ${alpha})`;
  }
}
