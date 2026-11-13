import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AbastService } from '../../core/services/abast.service';
import { CarroService } from '../../core/services/carro.service';
import { Carro } from '../../core/models/carro.model';
import { ToastService } from '../../core/services/toast.service';
import { SeletorItem } from '../../shared/seletor/seletor.component';
import { nomeVeiculo } from '../../core/utils/situacao.utils';
import { hojeISO } from '../../core/utils/data.utils';

@Component({
  standalone: false,
  selector: 'app-cadastro-abastecimento',
  templateUrl: './cadastro-abastecimento.component.html',
})
export class CadastroAbastecimentoComponent implements OnInit {
  abastForm: FormGroup;
  showModal = false;
  cars: Carro[] = [];
  selectedCar: Carro | null = null;
  loading = false;
  enviado = false;
  kmPuxado: number | null = null;
  ultimoLancado: Carro | null = null;
  readonly hoje = hojeISO();

  constructor(
    private fb: FormBuilder,
    private abastService: AbastService,
    private carroService: CarroService,
    private toastService: ToastService,
    private route: ActivatedRoute
  ) {
    this.abastForm = this.fb.group({
      carro: [null, Validators.required],
      kmAtual: [null, [Validators.required, Validators.min(1)]],
      litroComb: [null, [Validators.required, Validators.min(0.1)]],
      valorComb: [null, [Validators.required, Validators.min(0.1)]],
      dtAbast: [this.hoje],
    });
  }

  ngOnInit(): void {
    this.carroService.getList<Carro>().subscribe({
      next: (cars) => {
        this.cars = cars;
        const id = Number(this.route.snapshot.queryParamMap.get('carro'));
        const c = id ? cars.find(x => x.id === id) : null;
        if (c) this.selectCar(c);
      },
      error: () => this.toastService.showError('Não foi possível carregar os carros.')
    });
  }

  get itens(): SeletorItem[] {
    return this.cars.map(c => ({ id: c.id, titulo: nomeVeiculo(c.marca?.marca, c.modelo?.modelo), placa: c.placa }));
  }

  nome(c: Carro | null): string { return c ? nomeVeiculo(c.marca?.marca, c.modelo?.modelo) : ''; }

  erro(campo: string): boolean {
    const c = this.abastForm.get(campo);
    return !!c && c.invalid && (c.touched || this.enviado);
  }

  get kmMenor(): boolean {
    const km = Number(this.abastForm.value.kmAtual);
    return !!km && this.kmPuxado != null && km < this.kmPuxado;
  }

  get rodado(): number | null {
    const km = Number(this.abastForm.value.kmAtual);
    return km && this.kmPuxado != null && km >= this.kmPuxado ? km - this.kmPuxado : null;
  }

  escolher(item: SeletorItem) {
    const c = this.cars.find(x => x.id === item.id);
    if (c) this.selectCar(c);
  }

  selectCar(car: Carro): void {
    this.selectedCar = car;
    this.abastForm.patchValue({ carro: car });
    this.showModal = false;
    this.kmPuxado = null;
    this.carroService.getKmRecente(car.id).subscribe(num => this.kmPuxado = num);
  }

  /** Preço por litro calculado a partir do total pago, só para conferência. */
  get precoPorLitro(): number | null {
    const l = Number(this.abastForm.value.litroComb);
    const v = Number(this.abastForm.value.valorComb);
    return l > 0 && v > 0 ? v / l : null;
  }

  cadastrarAbastecimento() {
    this.enviado = true;
    this.abastForm.markAllAsTouched();
    if (this.abastForm.invalid) {
      this.toastService.showWarning(this.selectedCar ? 'Confira os campos destacados.' : 'Escolha o carro.');
      return;
    }
    if (this.kmMenor) {
      this.toastService.showWarning('O km informado é menor que o último registrado.');
      return;
    }
    this.loading = true;
    // o usuário responsável é definido pelo backend a partir do token
    this.abastService.post(this.abastForm.value).subscribe({
      next: () => {
        this.loading = false;
        this.toastService.showSuccess('Abastecimento lançado.');
        this.ultimoLancado = this.selectedCar;
        this.enviado = false;
        this.abastForm.reset({ carro: null, dtAbast: this.hoje });
        this.selectedCar = null;
        this.kmPuxado = null;
      },
      error: (err) => {
        this.loading = false;
        this.toastService.showError(typeof err?.error === 'string' && err.error ? err.error : 'Não foi possível lançar o abastecimento.');
      }
    });
  }
}
