import { Injectable } from '@angular/core';
import { BaseService } from '../../core/services/base.service';
import { AppConstants } from '../../core/constants/app.constants';
import { Carro } from '../../core/models/carro.model';
import { Observable } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class CarroService extends BaseService{

  constructor(http: HttpClient){
    super(http);
    this.path=AppConstants.CARRO_URL;
  }

  getKmRecente(id: number): Observable<number>{
    const url = `${AppConstants.CARRO_URL}/${id}/km/recente`;
    return this.http.get<number>(url)
  }

  postByEmpresa(data: Carro): Observable<Carro>{
    const url = `${AppConstants.CARRO_URL}`;

    return this.http.post<Carro>(url, data);
  }

}
