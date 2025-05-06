import { Injectable } from '@angular/core';
import { BaseService } from '../../core/services/base.service';
import { AppConstants } from '../../core/constants/app.constants';
import { Observable } from 'rxjs';
import { Modelo } from '../../core/models/modelo.model';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ModeloService extends BaseService{

  constructor(http: HttpClient){
    super(http);
    this.path=AppConstants.MODELO_URL;
  }

}
