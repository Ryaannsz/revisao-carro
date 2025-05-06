import { Injectable } from '@angular/core';
import { BaseService } from '../../core/services/base.service';
import { AppConstants } from '../../core/constants/app.constants';
import { Carro } from '../../core/models/carro.model';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class CarroService extends BaseService{

  constructor(http: HttpClient){
    super(http);
    this.path=AppConstants.CARRO_URL;
  }

  getKmRecente(id: number){
    return this.getWithId<number>(id, '/km/recente')
  }

}
