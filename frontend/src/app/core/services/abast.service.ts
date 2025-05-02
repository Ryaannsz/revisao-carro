// src/app/core/services/abast.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Abast } from '../models/abast.model';
import { Observable } from 'rxjs';
import { AppConstants } from '../constants/app.constants';

@Injectable({
  providedIn: 'root'
})
export class AbastService {

  constructor(private http: HttpClient) {}

  cadastrarAbast(abast: Partial<Abast>): Observable<Abast> {
    return this.http.post<Abast>(AppConstants.ABAST_URL, abast);
  }
}
