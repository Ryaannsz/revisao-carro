import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MarcaService } from '../../core/services/marca.service';
import { ModeloService } from '../../core/services/modelo.service'; 
import { CarroService } from '../../core/services/carro.service';
import { CommonModule } from '@angular/common';
import { Marca } from '../../core/models/marca.model';
import { Modelo } from '../../core/models/modelo.model';

@Component({
  selector: 'app-cadastrar-carro',
  templateUrl: './cadastro-carro.component.html',
  imports: [CommonModule, FormsModule, ReactiveFormsModule] 
})
export class CadastrarCarroComponent implements OnInit {

  carroForm: FormGroup;
  marcas: Marca[] = [];
  modelos: Modelo[] = [];

  constructor(
    private fb: FormBuilder,
    private marcaService: MarcaService,
    private modeloService: ModeloService,
    private carroService: CarroService
  ) {
    this.carroForm = this.fb.group({
      idMarca: ['', Validators.required],
      idModelo: ['', Validators.required],
      kmAdicionado: ['', [Validators.required, Validators.min(0)]],
      placa: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.carregarMarcas();
    this.carregarModelos();
  }



  carregarMarcas() {
    this.marcaService.getList<Marca>().subscribe({
      next: (data) => this.marcas = data,
      error: (err) => console.error('Erro ao carregar marcas', err)
    });
  }

  carregarModelos() {
    this.modeloService.getList<Modelo>().subscribe({
      next: (data) => this.modelos = data,
      error: (err) => console.error('Erro ao carregar modelos', err)
    });
  }

  cadastrarCarro() {
    if (this.carroForm.valid) {
      this.carroService.post(this.carroForm.value).subscribe({
        next: (res) => {
          console.log('Carro cadastrado:', res);
          this.carroForm.reset();
        },
        error: (err) => {
          if (err.status==409){
            alert('Placa já cadastrado!')
          }
          console.error('Erro ao cadastrar carro', err);
        }
      });
    }
  }
}
