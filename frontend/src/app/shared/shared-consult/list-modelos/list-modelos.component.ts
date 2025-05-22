import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Modelo } from '../../../core/models/modelo.model';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { debounceTime, distinctUntilChanged, map, Observable, startWith } from 'rxjs';
import { ModeloService } from '../../../core/services/modelo.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-list-modelos',
  standalone: false,
  templateUrl: './list-modelos.component.html',
  styleUrls: ['./list-modelos.component.css']
})
export class ListModelosComponent implements OnInit {

  @Input() label: string = 'Modelo';
  @Output() modeloSelecionado = new EventEmitter<Modelo>();

  modeloForm: FormGroup;
  modelos: Modelo[] = [];
  filteredModelos$!: Observable<Modelo[]>;
  showCreateButton = false;
  isDropdownOpen = false;

  constructor(
    private modeloService: ModeloService,
    private toastService: ToastService,
    private fb: FormBuilder
  ) {
    this.modeloForm = this.fb.group({
      modelo: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.modeloService.getList<Modelo>().subscribe(modelos => {
      this.modelos = modelos;
      this.initFilter();
    });
  }

  get modeloField(): FormControl {
    return this.modeloForm.get('modelo') as FormControl;
  }

  private initFilter() {
    this.filteredModelos$ = this.modeloField.valueChanges.pipe(
      startWith(''),
      debounceTime(300),
      distinctUntilChanged(),
      map(value => {
        const filterValue = (value || '').toLowerCase();

        const modelosValidos = this.modelos.filter(m => m && typeof m.modelo === 'string');

        const filtered = modelosValidos.filter(m =>
          m.modelo.toLowerCase().includes(filterValue)
        );

        this.showCreateButton = !!filterValue && !modelosValidos.some(
          m => m.modelo.toLowerCase() === filterValue
        );

        return filtered;
      })
    );
  }

  selectModelo(modelo: Modelo) {
    this.modeloField.setValue(modelo.modelo, { emitEvent: false });
    this.modeloSelecionado.emit(modelo);
    this.showCreateButton = false;
    this.isDropdownOpen = false;
  }

  onBlur() {
    setTimeout(() => {
      this.isDropdownOpen = false;
    }, 200);
  }

  criarNovoModelo() {
    const nome = this.modeloField.value;
    if (!nome) return;

    if (this.modeloForm.valid) {
      const novoModelo = { modelo: nome };
      this.modeloService.post<Modelo>(novoModelo).subscribe(modeloCriado => {
        this.toastService.showSuccess("Modelo cadastrado com sucesso!");
        this.modelos.push(modeloCriado);
        this.modeloField.setValue(modeloCriado.modelo, { emitEvent: false });
        this.modeloSelecionado.emit(modeloCriado);
        this.showCreateButton = false;
        this.isDropdownOpen = false;
      });
    }
  }
}