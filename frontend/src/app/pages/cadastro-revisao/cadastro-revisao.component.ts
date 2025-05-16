import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Carro } from '../../core/models/carro.model';
import { RevisaoService } from '../../core/services/revisao.service'; // Novo serviço para revisão
import { CarroService } from '../../core/services/carro.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-cadastro-revisao',
  standalone: false,
  templateUrl: './cadastro-revisao.component.html',
  styleUrl: './cadastro-revisao.component.css'
})
export class CadastroRevisaoComponent {
  revisaoForm: FormGroup;
  showModal = false;
  cars: Carro[] = [];
  filteredCars: Carro[] = [];
  searchTerm = '';
  selectedCar: Carro | null = null;

  constructor(
    private fb: FormBuilder,
    private revisaoService: RevisaoService, // Serviço novo
    private carroService: CarroService,
    private authService: AuthService
  ) {
    this.revisaoForm = this.fb.group({
      kmAtual: ['', [Validators.required, Validators.min(1)]],
      idUser: [this.authService.getUserInfo().idUser],
      dtRevisao: [''],
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
    this.revisaoForm.patchValue({ idCarro: car.idCarro });
    this.showModal = false;
  }

  cadastrarRevisao() {
    if (this.revisaoForm.valid) {
      this.revisaoService.post(this.revisaoForm.value).subscribe({
        next: () => {
          alert('Revisão registrada com sucesso!');
          this.revisaoForm.reset();
          this.selectedCar = null;
        },
        error: (err) => {
          console.error('Erro ao registrar revisão:', err);
        }
      });
    } else {
      alert('Por favor, preencha todos os campos corretamente.');
    }
  }
}
