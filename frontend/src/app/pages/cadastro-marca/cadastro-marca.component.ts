import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MarcaService } from '../../core/services/marca.service';

@Component({
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  selector: 'app-cadastro-marca',
  templateUrl: './cadastro-marca.component.html'
})
export class CadastroMarcaComponent {

  marcaForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private marcaService: MarcaService
  ) {
    this.marcaForm = this.fb.group({
      marca: ['', Validators.required]
    });
  }

  cadastrarMarca() {

    if (this.marcaForm.valid) {
      const novaMarca = { marca: this.marcaForm.value.marca };  

      console.log(novaMarca);

      this.marcaService.cadastrarMarca(novaMarca).subscribe({
        next: () => {
          alert('Marca cadastrada com sucesso!');
          this.marcaForm.reset();
        },
        error: (err) => {
          console.error('Erro ao cadastrar marca:', err);
        }
      });
    } else {
      alert('Por favor, preencha todos os campos.');
    }
  }
}
