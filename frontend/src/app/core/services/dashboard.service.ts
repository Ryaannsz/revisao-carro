import { Injectable } from "@angular/core";
import { BaseService } from "./base.service";
import { HttpClient, HttpParams } from "@angular/common/http";
import { AppConstants } from "../constants/app.constants";
import { Observable } from "rxjs";
import { DashboardResumo } from "../models/dashboard.resumo.model";
import { CarStatsResponse } from "../models/car.stats.model";
import { CarOption } from "../models/car.option.model";

@Injectable({
    providedIn: 'root'
})

export class DashboardService extends BaseService{

    constructor(http: HttpClient){
        super(http);
        this.path=AppConstants.DASHBOARD_URL
    }

    getResumo(): Observable<DashboardResumo> {
        return this.http.get<DashboardResumo>(`${this.path}/resumo`)
    }

    getCarStats(carIds: number[], months = 6, mode: 'sum'|'compare' = 'compare'): Observable<CarStatsResponse> {
        let params = new HttpParams().set('carIds', carIds.join(','));
        if (months !== undefined && months !== null) params = params.set('months', String(months));
        if (mode) params = params.set('mode', mode);
        return this.http.get<CarStatsResponse>(`${AppConstants.BASE_URL}/car-stats`, { params });
  }

  getCarOption(): Observable<CarOption[]> {
    const url = `${AppConstants.BASE_URL}/car-stats/carros`
    return this.http.get<CarOption[]>(url);
  }

}