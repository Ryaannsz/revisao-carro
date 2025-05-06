import { Injectable } from '@angular/core';
import { BaseService } from '../../core/services/base.service';
import { AppConstants } from '../../core/constants/app.constants';
import { Observable } from 'rxjs';
import { Marca } from '../../core/models/marca.model';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class MarcaService extends BaseService{

  constructor(http: HttpClient){
    super(http);
    this.path=AppConstants.MARCA_URL;
  }

}
