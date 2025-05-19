import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Carro } from '../../core/models/carro.model';
import { CarroService } from '../../core/services/carro.service';
import { Router } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { ToastService } from '../../core/services/toast.service';

@Component({
  standalone: false,
  selector: 'app-lista-carros',
  templateUrl: './lista-carros.component.html'
})
export class ListaCarrosComponent implements OnInit {
  carros: Carro[] = [];
  loading: boolean = false;

  constructor(
    private carroService: CarroService,
    private router: Router,
    private toastService: ToastService
  ) { }

  ngOnInit() {
    this.carregarCarros();
  }

  carregarCarros() {
    this.loading = true;
    this.carroService.getList<Carro>().subscribe({
      next: (data) => {
        this.carros = data;
        this.getKm()
        this.loading = false;
        this.toastService.showSuccess("Carros listados com sucesso!");
      },
      error: (err) => {
        console.error('Erro ao carregar carros:', err);
        this.loading = false;
        this.toastService.showError("Erro ao listar carros!")
      }
    });
  }

  getKm() {
    this.carros.map((car) => {
      this.carroService.getKmRecente(car.idCarro).subscribe({
        next: (res) => {
          car.kmAdicionado = res;
        }
      })
    })
  }

  irParaCarro(id: number) {
    this.router.navigate(['/carros/unico', id]);
  }
}
