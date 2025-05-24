import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';
import { BehaviorSubject } from 'rxjs';
import { Role } from '../models/role.enum';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly TOKEN_KEY = 'auth_token';
  private jwtHelper = new JwtHelperService();

  // BehaviorSubject para controlar o estado de login
  private loggedIn = new BehaviorSubject<boolean>(this.isAuthenticated());

  // Observable para ser escutado pelo componente
  isLoggedIn$ = this.loggedIn.asObservable();

  constructor() { }

  setToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
    this.loggedIn.next(true); // notifica que está logado
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    return token != null && !this.jwtHelper.isTokenExpired(token);
  }

  removeToken(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    this.loggedIn.next(false); // notifica que deslogou
  }

  getUserInfo(): any {
    const token = this.getToken();
    return token ? this.jwtHelper.decodeToken(token) : null;
  }

  getUserRole(): any {
    const user = this.getUserInfo();
    if (user.role === "USER")
      return false;
    else
      return true;
  }
}
