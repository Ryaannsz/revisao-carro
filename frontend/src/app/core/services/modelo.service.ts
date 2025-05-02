import { Injectable } from '@angular/core';
import { BaseService } from '../../core/services/base.service';
import { AppConstants } from '../../core/constants/app.constants';
import { Observable } from 'rxjs';
import { Modelo } from '../../core/models/modelo.model';

@Injectable({
  providedIn: 'root'
})
export class ModeloService {

  constructor(private baseService: BaseService) {}

  listarModelos(): Observable<Modelo[]> {
    return this.baseService.get<Modelo[]>(AppConstants.MODELO_URL);
  }

  buscarModeloPorId(id: number): Observable<Modelo> {
    return this.baseService.get<Modelo>(`${AppConstants.MODELO_URL}/${id}`);
  }

  cadastrarModelo(modelo: {modelo: string}): Observable<Modelo> {
    return this.baseService.post<Modelo>(AppConstants.MODELO_URL, modelo);
  }

  atualizarModelo(id: number, modelo: Modelo): Observable<Modelo> {
    return this.baseService.put<Modelo>(`${AppConstants.MODELO_URL}/${id}`, modelo);
  }

  deletarModelo(id: number): Observable<any> {
    return this.baseService.delete(`${AppConstants.MODELO_URL}/${id}`);
  }
}
