// src/app/core/services/abast.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Abast } from '../models/abast.model';
import { Observable } from 'rxjs';
import { AppConstants } from '../constants/app.constants';
import { BaseService } from './base.service';

@Injectable({
  providedIn: 'root'
})
export class AbastService extends BaseService{

  constructor(http: HttpClient){
    super(http);
    this.path=AppConstants.ABAST_URL;
  }

  abastecimentoCarro(id: number): Observable<Abast[]>{
    return this.getListWithId(id, '/carro')
  }

}
