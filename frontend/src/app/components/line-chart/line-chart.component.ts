import { Component, Input, AfterViewInit, OnChanges, OnDestroy, ElementRef, ViewChild } from '@angular/core';
import {
  Chart, LineController, LineElement, PointElement, BarController, BarElement,
  LinearScale, CategoryScale, Tooltip, Legend, Filler
} from 'chart.js';

Chart.register(LineController, LineElement, PointElement, BarController, BarElement, LinearScale, CategoryScale, Tooltip, Legend, Filler);

/** Paleta categórica validada (ordem fixa; nunca reciclar cores). */
export const SERIES = ['#2a78d6', '#eb6834', '#1baf7a', '#eda100', '#e87ba4', '#008300', '#6250d6', '#e34948'];

const INK = '#22302B';
const INK_SOFT = '#56665F';
const GRID = '#E7E9E3';

/**
 * Gráfico de linha ou barras com o visual do app: grade discreta, tooltip por índice
 * (cruzamento vertical), linhas de 2px e barras com cantos arredondados.
 */
@Component({
  selector: 'app-line-chart',
  template: `<div class="relative w-full" [style.height]="height"><canvas #canvas role="img" [attr.aria-label]="ariaLabel"></canvas></div>`,
  standalone: false
})
export class LineChartComponent implements AfterViewInit, OnChanges, OnDestroy {
  @ViewChild('canvas') canvas!: ElementRef<HTMLCanvasElement>;

  @Input() labels: string[] = [];
  @Input() datasets: Array<any> = [];
  /** options extras (mescladas por cima do padrão) */
  @Input() options: any = {};
  @Input() type: 'line' | 'bar' = 'line';
  @Input() stacked = false;
  @Input() height = '18rem';
  @Input() ariaLabel = 'Gráfico';

  private chart?: Chart;

  ngAfterViewInit(): void { this.buildChart(); }

  ngOnChanges(): void {
    if (!this.canvas) return;
    this.chart?.destroy();
    this.buildChart();
  }

  ngOnDestroy(): void { this.chart?.destroy(); }

  private buildChart(): void {
    const ctx = this.canvas.nativeElement.getContext('2d')!;
    const multi = (this.datasets ?? []).length > 1;
    const isBar = this.type === 'bar';

    const datasets = (this.datasets ?? []).map((d, i) => {
      const cor = d.borderColor ?? SERIES[i % SERIES.length];
      return isBar ? {
        label: d.label, data: d.data,
        backgroundColor: cor, borderColor: '#FFFFFF', borderWidth: this.stacked ? { top: 2 } : 0,
        borderRadius: 4, borderSkipped: 'bottom' as const, maxBarThickness: 36,
      } : {
        label: d.label, data: d.data,
        borderColor: cor, backgroundColor: cor, borderWidth: 2,
        pointRadius: (d.data?.length ?? 0) > 18 ? 0 : 3, pointHoverRadius: 5,
        pointBackgroundColor: cor, pointBorderColor: '#FFFFFF', pointBorderWidth: 2,
        tension: 0.25, fill: false, spanGaps: false,
      };
    });

    const base: any = {
      responsive: true,
      maintainAspectRatio: false,
      interaction: { mode: 'index', intersect: false },
      plugins: {
        legend: {
          display: multi, position: 'top', align: 'start',
          labels: { color: INK, boxWidth: 10, boxHeight: 10, usePointStyle: true, pointStyle: 'rectRounded', font: { family: 'Barlow', size: 13, weight: '600' } },
        },
        tooltip: {
          backgroundColor: INK, titleColor: '#FFFFFF', bodyColor: '#FFFFFF', padding: 10, cornerRadius: 6,
          titleFont: { family: 'Barlow', weight: '600' }, bodyFont: { family: 'Barlow' }, boxPadding: 4, usePointStyle: true,
        },
      },
      scales: {
        x: { stacked: this.stacked, grid: { display: false }, border: { color: GRID }, ticks: { color: INK_SOFT, font: { family: 'Barlow', size: 12 }, maxRotation: 0, autoSkipPadding: 12 } },
        y: { stacked: this.stacked, beginAtZero: true, grid: { color: GRID }, border: { display: false }, ticks: { color: INK_SOFT, font: { family: 'Barlow', size: 12 }, maxTicksLimit: 6 } },
      },
    };

    this.chart = new Chart(ctx, {
      type: this.type,
      data: { labels: this.labels ?? [], datasets: datasets as any },
      options: this.merge(base, this.options ?? {}),
    });
  }

  private merge(a: any, b: any): any {
    const out = { ...a };
    for (const k of Object.keys(b)) {
      out[k] = b[k] && typeof b[k] === 'object' && !Array.isArray(b[k]) && typeof b[k] !== 'function' && a[k] && typeof a[k] === 'object'
        ? this.merge(a[k], b[k]) : b[k];
    }
    return out;
  }
}
