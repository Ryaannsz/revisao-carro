// src/app/shared/components/line-chart/line-chart.component.ts
import { Component, Input, AfterViewInit, OnChanges, OnDestroy, SimpleChanges, ElementRef, ViewChild } from '@angular/core';
import { Chart, LineController, LineElement, PointElement, LinearScale, CategoryScale, Tooltip, Legend } from 'chart.js';

Chart.register(LineController, LineElement, PointElement, LinearScale, CategoryScale, Tooltip, Legend);

@Component({
  selector: 'app-line-chart',
  template: `<canvas #canvas class="w-full h-80"></canvas>`, // altura confortável; ajuste se quiser
  standalone: false
})
export class LineChartComponent implements AfterViewInit, OnChanges, OnDestroy {
  @ViewChild('canvas') canvas!: ElementRef<HTMLCanvasElement>;

  // labels e datasets vêm do container (Dashboard)
  @Input() labels: string[] = [];
  // deixe "any" para permitir passar cores, fill, tension etc. sem reclamar de tipo
  @Input() datasets: Array<any> = [];
  // options opcionais (tooltips, escala, currency, grande/pequeno, etc.)
  @Input() options: any = {};

  private chart?: Chart;

  ngAfterViewInit(): void {
    this.buildChart();
  }

  ngOnChanges(changes: SimpleChanges): void {
    // Se o canvas ainda não existe (ciclo inicial), não tenta recriar
    if (!this.canvas) return;

    // Se já há gráfico, destrói e recria (simples e robusto)
    if (this.chart) {
      this.chart.destroy();
    }
    this.buildChart();
  }

  ngOnDestroy(): void {
    if (this.chart) this.chart.destroy();
  }

  private buildChart(): void {
    const ctx = this.canvas.nativeElement.getContext('2d')!;
    this.chart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: this.labels ?? [],
        // repassa os datasets já com cores e configs vindas do pai
        datasets: (this.datasets ?? []).map(d => ({
          label: d.label,
          data: d.data,
          // pass-through de estilos (se não vier, Chart.js usa defaults)
          borderColor: d.borderColor,
          backgroundColor: d.backgroundColor,
          pointRadius: d.pointRadius,
          tension: d.tension,
          fill: d.fill
        }))
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: true } },
        scales: { x: { display: true }, y: { display: true, beginAtZero: true } },
        // mescla com options passadas pelo pai (pai sobrescreve)
        ...this.options
      }
    });
  }
}
