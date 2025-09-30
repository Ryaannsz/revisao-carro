// src/app/shared/components/line-chart/line-chart.component.ts
import { Component, Input, AfterViewInit, ElementRef, ViewChild } from '@angular/core';
import { Chart, LineController, LineElement, PointElement, LinearScale, CategoryScale, Tooltip, Legend } from 'chart.js';
Chart.register(LineController, LineElement, PointElement, LinearScale, CategoryScale, Tooltip, Legend);

@Component({
  selector: 'app-line-chart',
  template: `<canvas #canvas></canvas>`,
  standalone: false
})
export class LineChartComponent implements AfterViewInit {
  @ViewChild('canvas') canvas!: ElementRef<HTMLCanvasElement>;
  @Input() labels: string[] = [];
  @Input() datasets: { label: string; data: (number|null)[] }[] = [];

  chart!: Chart;

  ngAfterViewInit(): void {
    this.chart = new Chart(this.canvas.nativeElement, {
      type: 'line',
      data: { labels: this.labels, datasets: this.datasets.map(d => ({ label: d.label, data: d.data })) },
      options: {
        responsive: true,
        plugins: { legend: { display: true } },
        scales: { x: { display: true }, y: { display: true }}
      }
    });
  }
}
