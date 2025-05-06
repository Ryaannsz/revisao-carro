import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Carro } from '../../core/models/carro.model';
import { CarroService } from '../../core/services/carro.service';
import { Router } from '@angular/router';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  selector: 'app-lista-carros',
  templateUrl: './lista-carros.component.html'
})
export class ListaCarrosComponent implements OnInit {
  carros: Carro[] = [];

  constructor(
    private carroService: CarroService,
    private router: Router
  ) {}

  ngOnInit() {
    this.carregarCarros();
  }

  carregarCarros() {
    this.carroService.getList<Carro>().subscribe({
      next: (data) => {
        this.carros = data;
      },
      error: (err) => {
        console.error('Erro ao carregar carros:', err);
      }
    });
  }

  irParaCarro(id: number){
    this.router.navigate(['/carros/unico', id]);
  }
}
