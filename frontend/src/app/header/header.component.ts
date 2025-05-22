import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

@Component({
  selector: 'app-header',
  standalone: false,
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
  menuAberto = false;
  carroAberto = false;
  abastecimentoAberto = false;
  revisaoAberto = false;

  toggleMenu() {
    this.menuAberto = !this.menuAberto;
  }

  toggleSubmenu(section: string) {
    if (section === 'carro') this.carroAberto = !this.carroAberto;
    if (section === 'abastecimento') this.abastecimentoAberto = !this.abastecimentoAberto;
    if (section === 'revisao') this.revisaoAberto = !this.revisaoAberto;
  }
}

