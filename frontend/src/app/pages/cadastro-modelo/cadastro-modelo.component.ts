import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ModeloService } from '../../core/services/modelo.service';
import { Modelo } from '../../core/models/modelo.model';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../core/services/toast.service';

@Component({
  standalone: false,
  selector: 'app-cadastrar-modelo',
  templateUrl: './cadastro-modelo.component.html',
})
export class CadastrarModeloComponent implements OnInit {

  modeloForm: FormGroup;
  loading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private modeloService: ModeloService,
    private toastService: ToastService
  ) {
    this.modeloForm = this.fb.group({
      modelo: ['', Validators.required]
    });
  }

  ngOnInit() { }

  cadastrarModelo() {
    if (this.modeloForm.invalid) {
      this.modeloForm.markAllAsTouched();
      return;
    }
    this.loading = true;
    {
      const novoModelo = { modelo: this.modeloForm.value.modelo };

      this.modeloService.post(novoModelo).subscribe({
        next: (res) => {
          this.toastService.showSuccess('Modelo cadastrado.');
          this.modeloForm.reset();
          this.loading = false;
        },
        error: (err) => {
          this.loading = false;
          if (err.status == 409 || err.status == 400) this.toastService.showWarning(typeof err?.error === 'string' && err.error ? err.error : 'Esse modelo já existe.');
          else this.toastService.showError('Não foi possível cadastrar.');
          this.loading = false;
        }
      });
    }
  }
}
