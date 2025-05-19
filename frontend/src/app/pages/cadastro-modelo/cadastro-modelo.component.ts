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
    this.loading = true;
    if (this.modeloForm.valid) {
      const novoModelo = { modelo: this.modeloForm.value.modelo };

      this.modeloService.post(novoModelo).subscribe({
        next: (res) => {
          this.toastService.showSuccess('Modelo cadastrado com sucesso!');
          this.modeloForm.reset();
          this.loading = false;
        },
        error: (err) => {
          if (err.status == 409) {
            this.toastService.showWarning('Modelo já cadastrado!');
            this.loading = false;
          }
          this.toastService.showError('Erro ao cadastrar modelo', err);
          this.loading = false;
        }
      });
    }
  }
}
