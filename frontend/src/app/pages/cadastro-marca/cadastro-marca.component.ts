import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MarcaService } from '../../core/services/marca.service';
import { ToastService } from '../../core/services/toast.service';

@Component({
  standalone: false,
  selector: 'app-cadastro-marca',
  templateUrl: './cadastro-marca.component.html'
})
export class CadastroMarcaComponent {

  marcaForm: FormGroup;
  loading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private marcaService: MarcaService,
    private toastService: ToastService
  ) {
    this.marcaForm = this.fb.group({
      marca: ['', Validators.required]
    });
  }

  cadastrarMarca() {
    if (this.marcaForm.invalid) {
      this.marcaForm.markAllAsTouched();
      return;
    }
    this.loading = true;
    {
      const novaMarca = { marca: this.marcaForm.value.marca };


      this.marcaService.post(novaMarca).subscribe({
        next: () => {
          this.toastService.showSuccess('Marca cadastrada.');
          this.marcaForm.reset();
          this.loading = false;
        },
        error: (err) => {
          this.loading = false;
          if (err.status == 409 || err.status == 400) this.toastService.showWarning(typeof err?.error === 'string' && err.error ? err.error : 'Essa marca já existe.');
          else this.toastService.showError('Não foi possível cadastrar.');
          this.loading = false;
        }
      });
    }
  }
}
