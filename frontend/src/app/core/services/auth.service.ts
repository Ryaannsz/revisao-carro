// src/app/services/auth.service.ts
import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly TOKEN_KEY = 'auth_token';
  private jwtHelper = new JwtHelperService();

  constructor() { }

  // Salvar tokens
  setToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
    // Alternativa: sessionStorage.setItem(this.TOKEN_KEY, token);
  }

  // Obter token
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
    // Alternativa: return sessionStorage.getItem(this.TOKEN_KEY);
  }

  // Verificar se está autenticado
  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  // Remover token (logout)
  removeToken(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    // Alternativa: sessionStorage.removeItem(this.TOKEN_KEY);
  }

    getUserInfo(): any {
    const token = this.getToken();
    return token ? this.jwtHelper.decodeToken(token) : null;
  }
}