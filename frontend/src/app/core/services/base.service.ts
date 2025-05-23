import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AppConstants } from '../constants/app.constants';

@Injectable({
  providedIn: 'root'
})
export class BaseService {

  protected path: string = ''

  constructor(protected http: HttpClient) { }


  get<T>(endpoint: string = ''): Observable<T> {
    return this.http.get<T>(`${this.path}${endpoint}`);
  }


  post<T>(data: any, endpoint: string = ''): Observable<T> {
    return this.http.post<T>(`${this.path}${endpoint}`, data);
  }


  put<T>(data: any, endpoint: string = ''): Observable<T> {
    return this.http.put<T>(`${this.path}${endpoint}`, data);
  }


  delete<T>(endpoint: string = ''): Observable<T> {
    return this.http.delete<T>(`${this.path}/${endpoint}`);
  }

  getWithId<T>(id: number, endpoint: string = ''): Observable<T> {
    return this.http.get<T>(`${this.path}${endpoint}/${id}`);
  }

  getList<T>(endpoint: string = ''): Observable<T[]> {
    return this.http.get<T[]>(`${this.path}${endpoint}`);
  }

  getListWithId<T>(id: number, endpoint: string = ''): Observable<T[]> {
    return this.http.get<T[]>(`${this.path}${endpoint}/${id}`);
  }
}
