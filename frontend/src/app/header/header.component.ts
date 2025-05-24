import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastService } from '../core/services/toast.service';
import { AuthService } from '../core/services/auth.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-header',
  standalone: false,
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent implements OnInit, OnDestroy {
  menuAberto = false;
  carroAberto = false;
  abastecimentoAberto = false;
  revisaoAberto = false;
  logado = false;
  private subscription!: Subscription;

  constructor(private router: Router,
    private toastService: ToastService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.subscription = this.authService.isLoggedIn$.subscribe(status => {
      this.logado = status;
    });
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }


  toggleMenu() {
    this.menuAberto = !this.menuAberto;
  }

  toggleSubmenu(section: string) {
    if (section === 'carro') this.carroAberto = !this.carroAberto;
    if (section === 'abastecimento') this.abastecimentoAberto = !this.abastecimentoAberto;
    if (section === 'revisao') this.revisaoAberto = !this.revisaoAberto;
  }

  logout() {
    this.authService.removeToken();
    this.router.navigate(['/login']);
    this.toastService.showSuccess("Usuário desconectado com sucesso!");
  }
}

