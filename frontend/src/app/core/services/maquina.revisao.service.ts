import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { BaseService } from "./base.service";
import { AppConstants } from "../constants/app.constants";
import { RevisaoMaquina } from "../models/revisao.maquina.model";
import { ProximaRevisaoMaquina } from "../models/proxima.revisao.maquina.model";

@Injectable({
    providedIn: 'root'
})
export class RevisaoMaquinaService extends BaseService {

    constructor(http: HttpClient) {
        super(http);
        this.path = AppConstants.REVISAO_MAQUINA_URL;
    }

    listar(): Observable<RevisaoMaquina[]> {
        return this.http.get<RevisaoMaquina[]>(this.path);
    }

    porMaquina(maquinaId: number): Observable<RevisaoMaquina[]> {
        return this.http.get<RevisaoMaquina[]>(`${this.path}/maquina/${maquinaId}`);
    }

    registrar(dados: { maquina: { id: number }, horasAtual: number, dtRevisao?: string | null,
                       valor?: number | null, descricao?: string | null }): Observable<RevisaoMaquina> {
        return this.http.post<RevisaoMaquina>(this.path, dados);
    }

    proximas(): Observable<ProximaRevisaoMaquina[]> {
        return this.http.get<ProximaRevisaoMaquina[]>(`${this.path}/alarme`);
    }

    proxima(maquinaId: number): Observable<ProximaRevisaoMaquina> {
        return this.http.get<ProximaRevisaoMaquina>(`${this.path}/alarme/${maquinaId}`);
    }
}
