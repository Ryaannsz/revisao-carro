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

  patchRole(roles: string, id: number): Observable<User> {
    const url = `${AppConstants.USER_URL}/${id}`;
    console.log(roles);
    return this.http.patch<User>(url, { roles });
  }
}
