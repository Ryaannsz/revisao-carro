import { Injectable } from '@angular/core';
import { BaseService } from '../../core/services/base.service';
import { AppConstants } from '../../core/constants/app.constants';
import { Carro } from '../../core/models/carro.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CarroService {

  constructor(private baseService: BaseService) {}

  listarCarros(): Observable<Carro[]> {
    return this.baseService.get<Carro[]>(AppConstants.CARRO_URL);
  }

  buscarCarroPorId(id: number): Observable<Carro> {
    return this.baseService.get<Carro>(`${AppConstants.CARRO_URL}/${id}`);
  }

  cadastrarCarro(carro: Carro): Observable<Carro> {
    return this.baseService.post<Carro>(AppConstants.CARRO_URL, carro);
  }

  atualizarCarro(id: number, carro: Carro): Observable<Carro> {
    return this.baseService.put<Carro>(`${AppConstants.CARRO_URL}/${id}`, carro);
  }

  deletarCarro(id: number): Observable<any> {
    return this.baseService.delete(`${AppConstants.CARRO_URL}/${id}`);
  }
}
