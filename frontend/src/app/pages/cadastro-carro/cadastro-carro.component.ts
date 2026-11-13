import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CarroService } from '../../core/services/carro.service';
import { Marca } from '../../core/models/marca.model';
import { Modelo } from '../../core/models/modelo.model';
import { ToastService } from '../../core/services/toast.service';

@Component({
  standalone: false,
  selector: 'app-cadastrar-carro',
  templateUrl: './cadastro-carro.component.html',
  styleUrls: ['./cadastro-carro.component.css']
})
export class CadastrarCarroComponent {
  carroForm: FormGroup;
  loading = false;
  enviado = false;

  constructor(
    private fb: FormBuilder,
    private carroService: CarroService,
    private toastService: ToastService,
    private router: Router
  ) {
    this.carroForm = this.fb.group({
      placa: ['', [Validators.required, Validators.pattern(/^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$/)]],
      marca: [null, Validators.required],
      modelo: [null, Validators.required],
      kmAdicionado: [null, [Validators.required, Validators.min(0), Validators.max(9999999)]],
      alarmeRevisaoKm: [null, [Validators.min(500)]],
      alarmeRevisaoMes: [null, [Validators.min(1), Validators.max(36)]],
    });
  }

  get f() { return this.carroForm.controls; }

  erro(campo: string): boolean {
    const c = this.carroForm.get(campo);
    return !!c && c.invalid && (c.touched || this.enviado);
  }

  onMarcaSelecionado(marca: Marca) { this.carroForm.patchValue({ marca }); }
  onModeloSelecionado(modelo: Modelo) { this.carroForm.patchValue({ modelo }); }

  sanitizePlaca() {
    const c = this.carroForm.get('placa');
    const v = (c?.value || '').replace(/[^a-zA-Z0-9]/g, '').slice(0, 7).toUpperCase();
    c?.setValue(v, { emitEvent: false });
  }

  cadastrarCarro(continuar = false) {
    this.enviado = true;
    this.carroForm.markAllAsTouched();
    if (this.carroForm.invalid) {
      this.toastService.showWarning('Confira os campos destacados.');
      return;
    }
    const v = this.carroForm.value;
    const dados: any = {
      placa: v.placa,
      marca: v.marca,
      modelo: v.modelo,
      kmAdicionado: Number(v.kmAdicionado),
      ...(v.alarmeRevisaoKm ? { alarmeRevisaoKm: Number(v.alarmeRevisaoKm) } : {}),
      ...(v.alarmeRevisaoMes ? { alarmeRevisaoMes: Number(v.alarmeRevisaoMes) } : {}),
    };

    this.loading = true;
    this.carroService.postByEmpresa(dados).subscribe({
      next: () => {
        this.loading = false;
        this.toastService.showSuccess(`Carro ${v.placa} cadastrado.`);
        if (continuar) this.limparFormulario();
        else this.router.navigate(['/carros']);
      },
      error: (err) => {
        this.loading = false;
        const msg = typeof err?.error === 'string' && err.error ? err.error : null;
        if (err.status === 409 || /placa/i.test(msg ?? '')) this.toastService.showWarning(msg || 'Já existe um carro com essa placa.');
        else this.toastService.showError(msg || 'Não foi possível cadastrar o carro.');
      }
    });
  }

  limparFormulario() {
    this.enviado = false;
    this.carroForm.reset();
  }
}
