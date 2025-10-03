// src/app/pages/dashboard/dashboard.component.ts
import { Component, OnInit } from '@angular/core';
import { DashboardService } from '../../core/services/dashboard.service';
import { Observable, map } from 'rxjs';
import { MesValor } from '../../core/models/mes.valor.model';  // ajuste o caminho se necessário
import { AppConstants } from '../../core/constants/app.constants';

type ChartVM = {
  labels: string[];
  data: (number | null)[];
  labelsFiltered: string[];
  datasetsColored: any[];
};

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  standalone: false
})
export class DashboardComponent implements OnInit {
  vm$!: Observable<{
    gasto: ChartVM;
    litros: ChartVM;
    eficiencia: ChartVM;
    revisoes: ChartVM;
    proximas: any[];
    proximasSorted: any[];
  }>;

  Infinity = Number.POSITIVE_INFINITY;

  // UI state
  periodMonths: number | typeof Infinity = 6;
  fullscreenKey: 'gasto' | 'litros' | 'eficiencia' | 'revisoes' | null = null;

  // Paleta consistente (tons suaves)
  private colors = {
    gasto:    { border: '#2563eb', background: 'rgba(37, 99, 235, 0.10)' }, // blue-600
    litros:   { border: '#059669', background: 'rgba(5, 150, 105, 0.10)' }, // emerald-600
    efic:     { border: '#9333ea', background: 'rgba(147, 51, 234, 0.10)' }, // purple-600
    revisoes: { border: '#f59e0b', background: 'rgba(245, 158, 11, 0.10)' }  // amber-500
  };

  // Opções de gráfico (normais e ampliadas)
  chartOptionsCurrency      = this.makeChartOptions('R$');
  chartOptionsNumber        = this.makeChartOptions();
  chartOptionsCurrencyLarge = this.makeChartOptions('R$', true);
  chartOptionsNumberLarge   = this.makeChartOptions(undefined, true);

  constructor(private svc: DashboardService) {}

  ngOnInit(): void {
    this.reloadVM();
  }

  private formatMes(mesStr: string): string {
  const [ano, mes] = mesStr.split('-').map(Number);
  if (!ano || !mes) return mesStr; // fallback se não for no formato esperado
  return `${AppConstants.MESES_ABREV[mes - 1]}/${ano.toString().slice(-2)}`;
}

  setPeriod(months: number | typeof Infinity) {
    this.periodMonths = months;
    this.reloadVM();
  }

  private reloadVM() {
    const cutoff = this.periodMonths;

    this.vm$ = this.svc.getResumo().pipe(
      map(r => {
        const gastoArr: MesValor[]     = r.gastoMensal      ?? [];
        const litrosArr: MesValor[]    = r.litrosMensais    ?? [];
        const eficArr: MesValor[]      = r.eficienciaMensal ?? [];
        const revisoesArr: MesValor[]  = r.revisoesPorMes   ?? [];

        const gasto      = this.makeChartVM(gastoArr,     'Gasto',     this.colors.gasto,    cutoff);
        const litros     = this.makeChartVM(litrosArr,    'Litros',    this.colors.litros,   cutoff);
        const eficiencia = this.makeChartVM(eficArr,      'km/L',      this.colors.efic,     cutoff);
        const revisoes   = this.makeChartVM(revisoesArr,  'Revisões',  this.colors.revisoes, cutoff);

        // Próximas revisões: enriquece com status e ordena (dias até a data)
        const proximas = (r.proximasRevisoes ?? []).map((x: any) => {
          const days = this.daysFromToday(x.proximaRevisaoEstimada);
          const { text, klass } = this.statusFromDays(days);
          return { ...x, _days: days, _statusText: text, _statusClass: klass };
        });
        const proximasSorted = [...proximas].sort((a, b) => a._days - b._days);

        return { gasto, litros, eficiencia, revisoes, proximas, proximasSorted };
      })
    );
  }

  private makeChartVM(
    arr: MesValor[],
    label: string,
    color: { border: string; background: string },
    cutoff: number | typeof Infinity
  ): ChartVM {
    const allLabels = arr.map(x => this.formatMes(x.mes));
    const allData   = arr.map(x => x.valor as number | null); // aceita null para “gaps”

    const { labelsFiltered, dataFiltered } = this.sliceByMonths<(number | null)>(allLabels, allData, cutoff);

    return {
      labels: allLabels,
      data: allData,
      labelsFiltered,
      datasetsColored: [
        {
          label,
          data: dataFiltered,
          borderColor: color.border,
          backgroundColor: color.background,
          pointRadius: 3,
          tension: 0.3,
          fill: true,
          spanGaps: false // mantém quebras quando houver null
        }
      ]
    };
  }

  private sliceByMonths<T>(labels: string[], data: T[], cutoff: number | typeof Infinity) {
    if (cutoff === Infinity || labels.length <= cutoff) {
      return { labelsFiltered: labels, dataFiltered: data };
    }
    const start = Math.max(0, labels.length - Number(cutoff));
    return {
      labelsFiltered: labels.slice(start),
      dataFiltered: data.slice(start)
    };
  }

  private daysFromToday(dateLike: string | Date): number {
    const target = new Date(dateLike).getTime();
    const today  = new Date().setHours(0, 0, 0, 0);
    return Math.round((target - today) / 86400000);
  }

  private statusFromDays(days: number) {
    if (days < 0)  return { text: `Vencida há ${Math.abs(days)}d`, klass: 'bg-red-100 text-red-700' };
    if (days === 0) return { text: 'Vence hoje', klass: 'bg-amber-100 text-amber-700' };
    if (days <= 30) return { text: `Em ${days}d`, klass: 'bg-amber-100 text-amber-700' };
    return { text: `Em ${days}d`, klass: 'bg-emerald-100 text-emerald-700' };
  }

  openFullscreen(key: 'gasto' | 'litros' | 'eficiencia' | 'revisoes') {
    this.fullscreenKey = key;
  }
  closeFullscreen() {
    this.fullscreenKey = null;
  }
  get fullscreenTitle(): string {
    switch (this.fullscreenKey) {
      case 'gasto': return 'Gasto mensal (R$) – Ampliado';
      case 'litros': return 'Litros por mês (L) – Ampliado';
      case 'eficiencia': return 'Eficiência (km/L) – Ampliado';
      case 'revisoes': return 'Revisões por mês – Ampliado';
      default: return '';
    }
  }

  // Opções de Chart.js com tooltip pt-BR e suporte a null
  private makeChartOptions(currencySymbol?: string, large = false): any {
    const ticksFont = large ? 14 : 11;

    return {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: true, labels: { font: { size: ticksFont } } },
        tooltip: {
          callbacks: {
            label: (ctx: any) => {
              const v = ctx.parsed?.y;
              if (v == null) return `${ctx.dataset.label}: s/dado`;
              if (currencySymbol) {
                return `${ctx.dataset.label}: ${currencySymbol} ${v.toLocaleString('pt-BR', {
                  minimumFractionDigits: 2,
                  maximumFractionDigits: 2
                })}`;
              }
              return `${ctx.dataset.label}: ${Number(v).toLocaleString('pt-BR')}`;
            }
          }
        }
      },
      scales: {
        x: { ticks: { font: { size: ticksFont } } },
        y: {
          beginAtZero: true,
          ticks: {
            font: { size: ticksFont },
            callback: (value: number) =>
              currencySymbol
                ? `${currencySymbol} ${Number(value).toLocaleString('pt-BR')}`
                : Number(value).toLocaleString('pt-BR')
          }
        }
      },
      elements: { line: { borderWidth: 2 }, point: { radius: 3 } }
    };
  }
}
