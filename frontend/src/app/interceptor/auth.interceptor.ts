// src/app/interceptors/auth.interceptor.ts
import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../core/services/auth.service';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
    private readonly skipPaths = ['/auth/login', '/auth/refresh'];
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

   intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getToken();
    const empresaId = this.authService.getEmpresaId(); // lê do localStorage

    // 1) Header Authorization
    let request = token
      ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
      : req;

    // 2) Adiciona ?empresaId=... como query param (exceto rotas de auth)
    const shouldSkip = this.skipPaths.some(p => request.url.includes(p));
    if (!shouldSkip && empresaId != null && empresaId !== '' && !request.params.has('empresaId')) {
      const params = request.params.set('empresaId', String(empresaId));
      request = request.clone({ params });
    }

    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          this.authService.removeToken();
          this.router.navigate(['/login']);
        }
        return throwError(() => error);
      })
    );
  }
}