import { HttpClient } from "@angular/common/http";
import { BaseService } from "./base.service";
import { AppConstants } from "../constants/app.constants";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Revisao } from "../models/revisao.model";
import { ProximaRevisao } from "../models/proxima.revisao.model";

@Injectable({
    providedIn: 'root'
})

export class RevisaoService extends BaseService{

    constructor(http: HttpClient) {
        super(http);
        this.path=AppConstants.REVISAO_URL
    }

    revisaoCarro(id: number): Observable<Revisao[]>{
        return this.getListWithId(id);
    }

    calcularRevisao(): Observable<ProximaRevisao[]>{
        const url = `${AppConstants.REVISAO_URL}/alarme`
        return this.http.get<ProximaRevisao[]>(url);
    }
    calcularRevisaoById(carroId: number): Observable<ProximaRevisao[]>{
        const url = `${AppConstants.REVISAO_URL}/alarme/${carroId}`
        return this.http.get<ProximaRevisao[]>(url);
    }

}