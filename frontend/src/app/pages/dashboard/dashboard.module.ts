import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { SharedModule } from '../../shared/shared.module';
import { DashboardComponent } from './dashboard.component';
import { CarStatsComponent } from '../../components/car-stats/car-stats.component';
import { LineChartComponent } from '../../components/line-chart/line-chart.component';

/** Painel carregado sob demanda: o chart.js só é baixado por quem abre o painel. */
@NgModule({
  declarations: [DashboardComponent, CarStatsComponent, LineChartComponent],
  imports: [
    CommonModule, FormsModule, FontAwesomeModule, SharedModule,
    RouterModule.forChild([{ path: '', component: DashboardComponent }]),
  ],
})
export class DashboardModule { }
