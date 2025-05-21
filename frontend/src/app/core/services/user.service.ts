import { Injectable } from '@angular/core';
import { BaseService } from './base.service';
import { HttpClient } from '@angular/common/http';
import { AppConstants } from '../constants/app.constants';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root',
})
export class UserService extends BaseService {
  constructor(http: HttpClient) {
    super(http);
    this.path = AppConstants.USER_URL;
  }

  patchRole(role: string[], id: number): Observable<User> {
    const url = `${AppConstants.USER_URL}/${id}`;
    return this.http.patch<User>(this.path, { role });
  }
}
