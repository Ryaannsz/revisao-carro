import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Marca } from '../../core/models/marca.model';
import { Modelo } from '../../core/models/modelo.model';
import { MAQUINA_STATUS_INFO, MaquinaStatus, TIPOS_MAQUINA } from '../../core/models/maquina-status.enum';
import { MaquinaService } from '../../core/services/maquina.service';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-cadastro-maquina',
  standalone: false,
  templateUrl: './cadastro-maquina.component.html',
  styleUrl: './cadastro-maquina.component.css'
})
export class CadastroMaquinaComponent {
  form: FormGroup;
  loading = false;
  enviado = false;

  readonly tipos = TIPOS_MAQUINA;
  readonly intervalosHoras = [250, 500, 1000];
  readonly STATUS = MAQUINA_STATUS_INFO;
  readonly statusLista = Object.values(MaquinaStatus);
  readonly anoMax = new Date().getFullYear() + 1;

  constructor(
    private fb: FormBuilder,
    private maquinaService: MaquinaService,
    private toastService: ToastService,
    private router: Router
  ) {
    this.form = this.fb.group({
      identificacao: ['', [Validators.required, Validators.maxLength(60)]],
      tipo: [''],
      marca: [null],
      modelo: [null],
      ano: [null, [Validators.min(1950), Validators.max(this.anoMax)]],
      chassi: ['', [Validators.maxLength(40)]],
      horas: [null, [Validators.required, Validators.min(0)]],
      alarmeRevisaoHoras: [250, [Validators.min(10)]],
      alarmeRevisaoMes: [6, [Validators.min(1), Validators.max(36)]],
      status: [MaquinaStatus.DISPONIVEL],
    });
  }

  get f() { return this.form.controls; }

  erro(campo: string): boolean {
    const c = this.form.get(campo);
    return !!c && c.invalid && (c.touched || this.enviado);
  }

  setTipo(t: string) {
    this.form.patchValue({ tipo: this.f['tipo'].value === t ? '' : t });
  }

  onMarca(m: Marca) { this.form.patchValue({ marca: m }); }
  onModelo(m: Modelo) { this.form.patchValue({ modelo: m }); }

  salvar(continuar = false) {
    this.enviado = true;
    this.form.markAllAsTouched();
    if (this.form.invalid) {
      this.toastService.showWarning('Confira os campos destacados.');
      return;
    }
    const v = this.form.value;
    const dados = {
      identificacao: v.identificacao.trim(),
      tipo: v.tipo || null,
      marca: v.marca ? { id: v.marca.id } : null,
      modelo: v.modelo ? { id: v.modelo.id } : null,
      ano: v.ano ? Number(v.ano) : null,
      chassi: v.chassi?.trim() || null,
      horas: Number(v.horas),
      alarmeRevisaoHoras: v.alarmeRevisaoHoras ? Number(v.alarmeRevisaoHoras) : null,
      alarmeRevisaoMes: v.alarmeRevisaoMes ? Number(v.alarmeRevisaoMes) : null,
      status: v.status,
    } as any;

    this.loading = true;
    this.maquinaService.criar(dados).subscribe({
      next: (m) => {
        this.loading = false;
        this.toastService.showSuccess(`${dados.identificacao} cadastrada.`);
        if (continuar) {
          this.enviado = false;
          this.form.reset({ alarmeRevisaoHoras: 250, alarmeRevisaoMes: 6, status: MaquinaStatus.DISPONIVEL, tipo: '' });
        } else {
          this.router.navigate(['/maquinas', m.id]);
        }
      },
      error: (err) => {
        this.loading = false;
        this.toastService.showError(typeof err?.error === 'string' && err.error ? err.error : 'Não foi possível cadastrar a máquina.');
      }
    });
  }
}
