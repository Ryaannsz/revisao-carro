import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  selector: 'app-cadastro-modelo',
  templateUrl: './cadastro-modelo.component.html'
})
export class CadastroModeloComponent {
  modelo: string = '';

  constructor(private http: HttpClient) {}

  cadastrarModelo() {
    this.http.post('/modelo', { nome: this.modelo }).subscribe(() => {
      alert('Modelo cadastrado com sucesso!');
      this.modelo = '';
    });
  }
}
