import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Marca } from '../../../core/models/marca.model';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { debounceTime, distinctUntilChanged, map, Observable, startWith } from 'rxjs';
import { MarcaService } from '../../../core/services/marca.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-list-marca',
  standalone: false,
  templateUrl: './list-marca.component.html',
  styleUrl: './list-marca.component.css'
})
export class ListMarcaComponent implements OnInit {

  @Input() label: string = 'Marca';
  @Output() marcaSelecionado = new EventEmitter<Marca>();

  marcaForm: FormGroup;
  marcas: Marca[] = [];
  filteredMarca$!: Observable<Marca[]>;
  showCreateButton = false;
  isDropdownOpen = false;

  constructor(
    private marcaService: MarcaService,
    private toastService: ToastService,
    private fb: FormBuilder
  ) {
    this.marcaForm = this.fb.group({
      marca: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.marcaService.getList<Marca>().subscribe(marcas => {
      this.marcas = marcas;
      this.initFilter();
    });
  }

  get marcaField(): FormControl {
    return this.marcaForm.get('marca') as FormControl;
  }

  private initFilter() {
    this.filteredMarca$ = this.marcaField.valueChanges.pipe(
      startWith(''),
      debounceTime(300),
      distinctUntilChanged(),
      map(value => {
        const filterValue = (value || '').toLowerCase();

        const marcasValidos = this.marcas.filter(m => m && typeof m.marca === 'string');

        const filtered = marcasValidos.filter(m =>
          m.marca.toLowerCase().includes(filterValue)
        );

        this.showCreateButton = !!filterValue && !marcasValidos.some(
          m => m.marca.toLowerCase() === filterValue
        );

        return filtered;
      })
    );
  }

  selectMarca(marca: Marca) {
    this.marcaField.setValue(marca.marca, { emitEvent: false });
    this.marcaSelecionado.emit(marca);
    this.showCreateButton = false;
    this.isDropdownOpen = false;
  }

  onBlur() {
    setTimeout(() => {
      this.isDropdownOpen = false;
    }, 200);
  }

  criarNovaMarca() {
    const nome = this.marcaField.value;
    if (!nome) return;

    if (this.marcaForm.valid) {
      const novoMarca = { marca: nome };
      this.marcaService.post<Marca>(novoMarca).subscribe(marcaCriado => {
        this.toastService.showSuccess("Marca cadastrado com sucesso!");
        this.marcas.push(marcaCriado);
        this.marcaField.setValue(marcaCriado.marca, { emitEvent: false });
        this.marcaSelecionado.emit(marcaCriado);
        this.showCreateButton = false;
        this.isDropdownOpen = false;
      });
    }
  }
}
