import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AbastService } from '../../core/services/abast.service';
import { CarroService } from '../../core/services/carro.service'; // Novo serviço
import { Abast } from '../../core/models/abast.model';
import { User } from '../../core/models/user.model';
import { Carro } from '../../core/models/carro.model';
import { FormsModule } from '@angular/forms'; // Adicionar para suporte ao ngModel
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';

@Component({
  standalone: false,
  selector: 'app-cadastro-abastecimento',
  templateUrl: './cadastro-abastecimento.component.html',
})
export class CadastroAbastecimentoComponent implements OnInit {
  abastForm: FormGroup;
  showModal = false;
  cars: Carro[] = [];
  filteredCars: Carro[] = [];
  searchTerm = '';
  selectedCar: Carro | null = null;
  loading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private abastService: AbastService,
    private carroService: CarroService,
    private authService: AuthService,
    private toastService: ToastService
  ) {
    this.abastForm = this.fb.group({
      litroComb: ['', [Validators.required, Validators.min(0.1)]],
      valorComb: ['', [Validators.required, Validators.min(0.1)]],
      kmAtual: ['', [Validators.required, Validators.min(1)]],
      idUser: [this.authService.getUserInfo().idUser],
      dtAbast: [''],
      idCarro: ['', Validators.required]
    });
  }



  ngOnInit(): void {
    this.carroService.getList<Carro>().subscribe({
      next: (cars) => {
        this.cars = cars;
        this.filteredCars = [...cars];
      },
      error: (err) => console.error('Erro ao buscar carros:', err)
    });
  }

  filterCars(): void {
    const term = this.searchTerm.toLowerCase();
    this.filteredCars = this.cars.filter(car =>
      car.marca.marca.toLowerCase().includes(term) ||
      car.modelo.modelo.toLowerCase().includes(term) ||
      car.placa.toLowerCase().includes(term)
    );
  }

  openModal(): void {
    this.showModal = true;
    this.searchTerm = '';
    this.filterCars();
  }

  selectCar(car: Carro): void {
    this.selectedCar = car;
    this.abastForm.patchValue({ idCarro: car.idCarro });
    this.showModal = false;
  }

  cadastrarAbastecimento() {
    this.loading = true;
    if (this.abastForm.valid) {
      this.abastService.post(this.abastForm.value).subscribe({
        next: () => {
          this.toastService.showSuccess('Abastecimento registrado com sucesso!');
          this.abastForm.reset();
          this.selectedCar = null;
          this.loading = false;
        },
        error: (err) => {
          this.toastService.showError('Erro ao registrar abastecimento:');
          this.loading = false;
        }
      });
    } else {
      alert('Por favor, preencha todos os campos corretamente.');
    }
  }
}