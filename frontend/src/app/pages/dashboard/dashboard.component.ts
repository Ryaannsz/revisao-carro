import { Component, OnInit } from '@angular/core';
import { DashboardService } from '../../core/services/dashboard.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  standalone: false
})
export class DashboardComponent implements OnInit {
  vm$!: Observable<{
    gasto: any;
    litros: any;
    eficiencia: any;
    revisoes: any;
    proximas: any;
  }>;

  constructor(private svc: DashboardService) {}

  ngOnInit(): void {
    this.vm$ = this.svc.getResumo().pipe(
      map(r => ({
        gasto: {
          labels: r.gastoMensal.map(x => x.mes),
          datasets: [{ label: 'Gasto', data: r.gastoMensal.map(x => x.valor) }]
        },
        litros: {
          labels: r.litrosMensais.map(x => x.mes),
          datasets: [{ label: 'Litros', data: r.litrosMensais.map(x => x.valor) }]
        },
        eficiencia: {
          labels: r.eficienciaMensal.map(x => x.mes),
          datasets: [{ label: 'km/L', data: r.eficienciaMensal.map(x => x.valor) }]
        },
        revisoes: {
          labels: r.revisoesPorMes.map(x => x.mes),
          datasets: [{ label: 'Revisões', data: r.revisoesPorMes.map(x => x.valor) }]
        },
        proximas: r.proximasRevisoes
      }))
    );
  }
}
