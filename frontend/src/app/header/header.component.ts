import { Component, HostListener, OnDestroy, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { Subscription, filter } from 'rxjs';
import { ToastService } from '../core/services/toast.service';
import { AuthService } from '../core/services/auth.service';

interface NavItem { label: string; link: string; icon: string; }
interface NavGrupo { titulo: string; itens: NavItem[]; admin?: boolean; }

@Component({
  selector: 'app-header',
  standalone: false,
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent implements OnInit, OnDestroy {
  logado = false;
  isSuper = false;
  nome = '';
  menuAberto = false;
  lancarAberto = false;

  readonly grupos: NavGrupo[] = [
    {
      titulo: 'Frota', itens: [
        { label: 'Carros', link: '/carros', icon: 'car' },
        { label: 'Máquinas', link: '/maquinas', icon: 'tractor' },
      ]
    },
    {
      titulo: 'Lançar', itens: [
        { label: 'Abastecimento', link: '/abast', icon: 'gas-pump' },
        { label: 'Revisão de carro', link: '/revisao/cadastrar', icon: 'wrench' },
        { label: 'Manutenção de máquina', link: '/manutencao/maquina', icon: 'tools' },
      ]
    },
    {
      titulo: 'Histórico', itens: [
        { label: 'Abastecimentos', link: '/abastecimento/registros', icon: 'clock-rotate-left' },
        { label: 'Revisões', link: '/revisao/registros', icon: 'clock-rotate-left' },
      ]
    },
    {
      titulo: 'Gestão', admin: true, itens: [
        { label: 'Painel', link: '/dashboard', icon: 'chart-line' },
        { label: 'Usuários', link: '/usuario/cadastrados', icon: 'users' },
      ]
    },
  ];

  readonly lancamentos = this.grupos[1].itens;

  private subs = new Subscription();

  constructor(private router: Router,
    private toastService: ToastService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.subs.add(this.authService.isLoggedIn$.subscribe(status => {
      this.logado = status;
      this.isSuper = this.authService.getUserRole();
      this.nome = this.authService.getUserInfo()?.nome ?? '';
    }));
    this.subs.add(this.router.events.pipe(filter(e => e instanceof NavigationEnd)).subscribe(() => {
      this.menuAberto = false;
      this.lancarAberto = false;
    }));
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }

  get gruposVisiveis(): NavGrupo[] {
    return this.grupos.filter(g => !g.admin || this.isSuper);
  }

  get iniciais(): string {
    return this.nome.trim().split(/\s+/).slice(0, 2).map(p => p[0] ?? '').join('').toUpperCase() || '?';
  }

  toggleMenu() {
    this.menuAberto = !this.menuAberto;
    this.lancarAberto = false;
  }

  toggleLancar() {
    this.lancarAberto = !this.lancarAberto;
    this.menuAberto = false;
  }

  @HostListener('document:keydown.escape')
  fecharTudo() {
    this.menuAberto = false;
    this.lancarAberto = false;
  }

  logout() {
    this.authService.removeToken();
    this.fecharTudo();
    this.router.navigate(['/login']);
    this.toastService.showSuccess('Você saiu da conta.');
  }
}
