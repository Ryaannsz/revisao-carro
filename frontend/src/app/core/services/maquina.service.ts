import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable } from "rxjs";
import { BaseService } from "./base.service";
import { AppConstants } from "../constants/app.constants";
import { Maquina } from "../models/maquina.model";
import { MaquinaStatus } from "../models/maquina-status.enum";
import { LeituraHorimetro } from "../models/leitura.horimetro.model";

@Injectable({
    providedIn: 'root'
})
export class MaquinaService extends BaseService {

    constructor(http: HttpClient) {
        super(http);
        this.path = AppConstants.MAQUINA_URL;
    }

    listar(): Observable<Maquina[]> {
        return this.http.get<Maquina[]>(this.path);
    }

    buscar(id: number): Observable<Maquina> {
        return this.http.get<Maquina>(`${this.path}/${id}`);
    }

    criar(maquina: Partial<Maquina>): Observable<Maquina> {
        return this.http.post<Maquina>(this.path, maquina);
    }

    atualizar(id: number, dados: Partial<Maquina>): Observable<Maquina> {
        return this.http.put<Maquina>(`${this.path}/${id}`, dados);
    }

    atualizarStatus(id: number, status: MaquinaStatus): Observable<void> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        return this.http.put<void>(`${this.path}/status/${id}`, JSON.stringify(status), { headers });
    }

    horasRecentes(id: number): Observable<number> {
        return this.http.get<number>(`${this.path}/${id}/horas/recente`);
    }

    leituras(id: number): Observable<LeituraHorimetro[]> {
        return this.http.get<LeituraHorimetro[]>(`${this.path}/${id}/horas`);
    }

    registrarLeitura(id: number, horas: number, dtLeitura?: string | null): Observable<LeituraHorimetro> {
        return this.http.post<LeituraHorimetro>(`${this.path}/${id}/horas`, { horas, dtLeitura: dtLeitura || null });
    }
}
