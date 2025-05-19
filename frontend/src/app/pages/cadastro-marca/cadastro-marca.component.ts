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
    this.loading = true;
    if (this.marcaForm.valid) {
      const novaMarca = { marca: this.marcaForm.value.marca };


      this.marcaService.post(novaMarca).subscribe({
        next: () => {
          this.toastService.showSuccess("Marca cadastrada com sucesso!");
          this.marcaForm.reset();
          this.loading = false;
        },
        error: (err) => {
          if (err.status == 409) {
            this.toastService.showWarning('Marca já cadastrado!')
            this.loading = false;
          }

          this.toastService.showError('Erro ao cadastrar marca:', err);
          this.loading = false;
        }
      });
    } else {
      alert('Por favor, preencha todos os campos.');
    }
  }
}
