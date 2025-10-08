import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MarcaService } from '../../core/services/marca.service';
import { ModeloService } from '../../core/services/modelo.service';
import { CarroService } from '../../core/services/carro.service';
import { CommonModule } from '@angular/common';
import { Marca } from '../../core/models/marca.model';
import { Modelo } from '../../core/models/modelo.model';
import { ToastService } from '../../core/services/toast.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  standalone: false,
  selector: 'app-cadastrar-carro',
  templateUrl: './cadastro-carro.component.html',
  styleUrls: ['./cadastro-carro.component.css']
})
export class CadastrarCarroComponent implements OnInit {
  carroForm: FormGroup;
  marcas: Marca[] = [];
  modelos: Modelo[] = [];
  loading: boolean = false;
  
  // Contador de campos preenchidos para barra de progresso
  private camposPreenchidos: number = 0;

  constructor(
    private fb: FormBuilder,
    private marcaService: MarcaService,
    private modeloService: ModeloService,
    private carroService: CarroService,
    private toastService: ToastService,
    private authService: AuthService
  ) {
    this.carroForm = this.fb.group({
      marca: ['', Validators.required],
      modelo: ['', Validators.required],
      kmAdicionado: ['', [Validators.required, Validators.min(0), Validators.max(999999)]],
      placa: ['', [Validators.required, Validators.minLength(7), Validators.maxLength(7)]],
    });
    
    // Monitora mudanças no formulário para atualizar progresso
    this.carroForm.valueChanges.subscribe(() => {
      this.atualizarProgresso();
    });
  }

  ngOnInit() {
    this.carregarMarcas();
    this.carregarModelos();
  }

  onModeloSelecionado(modelo: Modelo) {
    this.carroForm.patchValue({ modelo: modelo });
    this.toastService.showSuccess(`Modelo ${modelo.modelo} selecionado`);
  }

  onMarcaSelecionado(marca: Marca) {
    this.carroForm.patchValue({ marca: marca });
    this.toastService.showSuccess(`Marca ${marca.marca} selecionada`);
  }

  carregarMarcas() {
    this.loading = true;
    this.marcaService.getList<Marca>().subscribe({
      next: (data) => {
        this.marcas = data;
        this.loading = false;
      },
      error: (err) => {
        this.toastService.showError('Erro ao carregar marcas. Tente novamente.');
        this.loading = false;
      }
    });
  }

  carregarModelos() {
    this.loading = true;
    this.modeloService.getList<Modelo>().subscribe({
      next: (data) => {
        this.modelos = data;
        this.loading = false;
      },
      error: (err) => {
        this.toastService.showError('Erro ao carregar modelos. Tente novamente.');
        this.loading = false;
      }
    });
  }

  cadastrarCarro() {
    // Marca todos os campos como tocados para mostrar erros
    Object.keys(this.carroForm.controls).forEach(key => {
      this.carroForm.get(key)?.markAsTouched();
    });

    if (this.carroForm.valid) {
      this.loading = true;
      
      try {
        const placaFormatada = this.formatarPlaca(this.carroForm.get('placa')?.value);
        const dadosCarro = {
          ...this.carroForm.value,
          placa: placaFormatada
        };

        this.carroService.postByEmpresa(dadosCarro).subscribe({
          next: (res) => {
            this.toastService.showSuccess('Veículo cadastrado com sucesso!');
            this.limparFormulario();
            this.loading = false;
            
            // Opcional: Redirecionar após sucesso
            // this.router.navigate(['/veiculos']);
          },
          error: (err) => {
            this.loading = false;
            
            if (err.status === 409) {
              this.toastService.showWarning('Esta placa já está cadastrada no sistema');
            } else if (err.status === 400) {
              this.toastService.showError('Dados inválidos. Verifique as informações');
            } else if (err.status === 401) {
              this.toastService.showError('Sessão expirada. Faça login novamente');
            } else {
              this.toastService.showError('Erro ao cadastrar veículo. Tente novamente');
            }
          }
        });
      } catch (error) {
        this.loading = false;
        this.toastService.showError('Placa inválida. Deve conter 7 caracteres');
      }
    } else {
      this.toastService.showWarning('Preencha todos os campos obrigatórios');
    }
  }

  sanitizePlaca() {
    const placaControl = this.carroForm.get('placa');
    const rawValue = placaControl?.value || '';
    
    // Remove caracteres especiais e limita a 7 caracteres
    let sanitized = rawValue.replace(/[^a-zA-Z0-9]/g, '');
    sanitized = sanitized.substring(0, 7).toUpperCase();
    
    placaControl?.setValue(sanitized, { emitEvent: false });
  }

  formatarPlaca(placa: string): string {
    if (placa.length !== 7) {
      throw new Error("Placa deve conter exatamente 7 caracteres");
    }
    
    // Formato Mercosul: ABC1D23 ou formato antigo: ABC-1234
    const parte1 = placa.substring(0, 3).toUpperCase();
    const parte2 = placa.substring(3).toUpperCase();
    
    // Detecta se é placa Mercosul (4º caractere é letra)
    const isMercosul = /[A-Z]/.test(placa.charAt(4));
    
    if (isMercosul) {
      // Formato Mercosul: ABC1D23
      return `${parte1}${parte2}`;
    } else {
      // Formato antigo: ABC-1234
      return `${parte1}-${parte2}`;
    }
  }

  limparFormulario() {
    this.carroForm.reset();
    Object.keys(this.carroForm.controls).forEach(key => {
      this.carroForm.get(key)?.setErrors(null);
      this.carroForm.get(key)?.markAsUntouched();
    });
  }

  // Calcula a porcentagem de progresso do formulário
  getProgressPercentage(): number {
    let filled = 0;
    const total = 4; // Total de campos obrigatórios
    
    if (this.carroForm.get('marca')?.value) filled++;
    if (this.carroForm.get('modelo')?.value) filled++;
    if (this.carroForm.get('kmAdicionado')?.value) filled++;
    if (this.carroForm.get('placa')?.value?.length === 7) filled++;
    
    return Math.round((filled / total) * 100);
  }

  private atualizarProgresso() {
    // Força atualização da view quando o progresso muda
    this.camposPreenchidos = this.getProgressPercentage();
  }

  // Validação customizada para placa (opcional)
  validarPlaca(placa: string): boolean {
    // Regex para placas brasileiras (antiga e Mercosul)
    const placaAntiga = /^[A-Z]{3}[0-9]{4}$/;
    const placaMercosul = /^[A-Z]{3}[0-9]{1}[A-Z]{1}[0-9]{2}$/;
    
    return placaAntiga.test(placa) || placaMercosul.test(placa);
  }
}