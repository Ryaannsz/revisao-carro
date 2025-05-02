import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  selector: 'app-cadastro-carro',
  templateUrl: './cadastro-carro.component.html'
})
export class CadastroCarroComponent implements OnInit {
  marcas: any[] = [];
  modelos: any[] = [];
  carro = {
    marcaId: '',
    modeloId: '',
    km: '',
    placa: ''
  };

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.http.get<any[]>('/marca').subscribe(data => this.marcas = data);
    this.http.get<any[]>('/modelo').subscribe(data => this.modelos = data);
  }

  cadastrarCarro() {
    this.http.post('/carro', this.carro).subscribe(() => {
      alert('Carro cadastrado com sucesso!');
      this.carro = { marcaId: '', modeloId: '', km: '', placa: '' };
    });
  }
}
