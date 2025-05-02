import { Injectable } from '@angular/core';
import { BaseService } from '../../core/services/base.service';
import { AppConstants } from '../../core/constants/app.constants';
import { Observable } from 'rxjs';
import { Marca } from '../../core/models/marca.model';

@Injectable({
  providedIn: 'root'
})
export class MarcaService {

  constructor(private baseService: BaseService) {}

  listarMarcas(): Observable<Marca[]> {
    return this.baseService.get<Marca[]>(AppConstants.MARCA_URL);
  }

  buscarMarcaPorId(id: number): Observable<Marca> {
    return this.baseService.get<Marca>(`${AppConstants.MARCA_URL}/${id}`);
  }

  cadastrarMarca(marca: { marca: string }): Observable<Marca> {
    return this.baseService.post<Marca>(AppConstants.MARCA_URL, marca);
  }

  atualizarMarca(id: number, marca: Marca): Observable<Marca> {
    return this.baseService.put<Marca>(`${AppConstants.MARCA_URL}/${id}`, marca);
  }

  deletarMarca(id: number): Observable<any> {
    return this.baseService.delete(`${AppConstants.MARCA_URL}/${id}`);
  }
}
