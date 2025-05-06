import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ModeloService } from '../../core/services/modelo.service';
import { Modelo } from '../../core/models/modelo.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-cadastrar-modelo',
  templateUrl: './cadastro-modelo.component.html',
  imports: [CommonModule, FormsModule, ReactiveFormsModule] 
})
export class CadastrarModeloComponent implements OnInit {

  modeloForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private modeloService: ModeloService
  ) {
    this.modeloForm = this.fb.group({
      modelo: ['', Validators.required]
    });
  }

  ngOnInit() {}

  cadastrarModelo() {
    if (this.modeloForm.valid) {
      const novoModelo = { modelo: this.modeloForm.value.modelo };

      this.modeloService.post(novoModelo).subscribe({
        next: (res) => {
          console.log('Modelo cadastrado:', res);
          this.modeloForm.reset();
        },
        error: (err) => {
          console.error('Erro ao cadastrar modelo', err);
        }
      });
    }
  }
}
