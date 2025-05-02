import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  selector: 'app-cadastro-marca',
  templateUrl: './cadastro-marca.component.html'
})
export class CadastroMarcaComponent {
  marca: string = '';

  constructor(private http: HttpClient) {}

  cadastrarMarca() {
    this.http.post('/marca', { nome: this.marca }).subscribe(() => {
      alert('Marca cadastrada com sucesso!');
      this.marca = '';
    });
  }
}
