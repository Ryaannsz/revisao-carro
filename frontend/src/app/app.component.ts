import { Component } from '@angular/core';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: false,
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  logado$;

  constructor(authService: AuthService) {
    this.logado$ = authService.isLoggedIn$;
  }
}
