import { Injectable } from "@angular/core";
import { BaseService } from "./base.service";
import { HttpClient } from "@angular/common/http";
import { AppConstants } from "../constants/app.constants";
import { Observable } from "rxjs";
import { DashboardResumo } from "../models/dashboard.resumo.model";

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

}