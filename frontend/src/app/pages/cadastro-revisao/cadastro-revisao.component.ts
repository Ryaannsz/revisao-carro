import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Carro } from '../../core/models/carro.model';
import { RevisaoService } from '../../core/services/revisao.service';
import { CarroService } from '../../core/services/carro.service';
import { ToastService } from '../../core/services/toast.service';
import { SeletorItem } from '../../shared/seletor/seletor.component';
import { nomeVeiculo } from '../../core/utils/situacao.utils';
import { hojeISO } from '../../core/utils/data.utils';

@Component({
  selector: 'app-cadastro-revisao',
  standalone: false,
  templateUrl: './cadastro-revisao.component.html',
  styleUrl: './cadastro-revisao.component.css'
})
export class CadastroRevisaoComponent implements OnInit {
  revisaoForm: FormGroup;
  showModal = false;
  cars: Carro[] = [];
  selectedCar: Carro | null = null;
  loading = false;
  enviado = false;
  kmPuxado: number | null = null;
  ultimoLancado: Carro | null = null;
  readonly hoje = hojeISO();

  /** Atalhos para descrever o serviço sem digitar tudo no celular. */
  readonly sugestoes = ['Troca de óleo e filtro', 'Filtro de ar', 'Pastilhas de freio', 'Alinhamento e balanceamento', 'Pneus', 'Correia dentada', 'Revisão completa'];

  constructor(
    private fb: FormBuilder,
    private revisaoService: RevisaoService,
    private carroService: CarroService,
    private toastService: ToastService,
    private route: ActivatedRoute
  ) {
    this.revisaoForm = this.fb.group({
      carro: [null, Validators.required],
      kmAtual: [null, [Validators.required, Validators.min(1)]],
      dtRevisao: [this.hoje],
      valor: [null, [Validators.min(0)]],
      descricao: ['', [Validators.maxLength(1000)]],
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
    const c = this.revisaoForm.get(campo);
    return !!c && c.invalid && (c.touched || this.enviado);
  }

  get kmMenor(): boolean {
    const km = Number(this.revisaoForm.value.kmAtual);
    return !!km && this.kmPuxado != null && km < this.kmPuxado;
  }

  escolher(item: SeletorItem) {
    const c = this.cars.find(x => x.id === item.id);
    if (c) this.selectCar(c);
  }

  selectCar(car: Carro): void {
    this.selectedCar = car;
    this.revisaoForm.patchValue({ carro: car });
    this.showModal = false;
    this.kmPuxado = null;
    this.carroService.getKmRecente(car.id).subscribe(num => this.kmPuxado = num);
  }

  adicionarSugestao(s: string) {
    const atual: string = (this.revisaoForm.value.descricao ?? '').trim();
    if (atual.toLowerCase().includes(s.toLowerCase())) return;
    this.revisaoForm.patchValue({ descricao: atual ? `${atual}, ${s.toLowerCase()}` : s });
  }

  cadastrarRevisao() {
    this.enviado = true;
    this.revisaoForm.markAllAsTouched();
    if (this.revisaoForm.invalid) {
      this.toastService.showWarning(this.selectedCar ? 'Confira os campos destacados.' : 'Escolha o carro.');
      return;
    }
    if (this.kmMenor) {
      this.toastService.showWarning('O km informado é menor que o último registrado.');
      return;
    }
    this.loading = true;
    // o usuário responsável é definido pelo backend a partir do token
    this.revisaoService.post(this.revisaoForm.value).subscribe({
      next: () => {
        this.loading = false;
        this.toastService.showSuccess('Revisão registrada.');
        this.ultimoLancado = this.selectedCar;
        this.enviado = false;
        this.revisaoForm.reset({ carro: null, dtRevisao: this.hoje, descricao: '' });
        this.selectedCar = null;
        this.kmPuxado = null;
      },
      error: (err) => {
        this.loading = false;
        this.toastService.showError(typeof err?.error === 'string' && err.error ? err.error : 'Não foi possível registrar a revisão.');
      }
    });
  }
}
