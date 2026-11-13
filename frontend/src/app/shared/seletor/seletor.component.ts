import { Component, EventEmitter, HostListener, Input, OnChanges, Output } from '@angular/core';

export interface SeletorItem {
  id: number;
  titulo: string;
  /** texto secundário (tipo, chassi…) */
  sub?: string | null;
  /** placa do carro, exibida no formato de placa */
  placa?: string | null;
  /** etiqueta à direita (status) */
  tag?: { label: string; classe: string } | null;
}

/** Lista pesquisável em folha (celular) / modal (desktop) para escolher um carro ou máquina. */
@Component({
  selector: 'app-seletor',
  standalone: false,
  template: `
    <div *ngIf="aberto" class="overlay" (click)="fechar.emit()">
      <div class="sheet sm:max-w-xl" role="dialog" aria-modal="true" [attr.aria-label]="titulo" (click)="$event.stopPropagation()">
        <div class="sheet-head">
          <h2 class="sheet-title">{{ titulo }}</h2>
          <button type="button" class="icon-btn -mr-2" (click)="fechar.emit()" aria-label="Fechar">
            <fa-icon [icon]="['fas', 'times']"></fa-icon>
          </button>
        </div>
        <div class="px-5 pt-4 pb-2">
          <label class="relative block">
            <span class="sr-only">Pesquisar</span>
            <fa-icon [icon]="['fas', 'search']" class="absolute left-3.5 top-1/2 -translate-y-1/2 text-ink-faint"></fa-icon>
            <input #busca type="search" class="input pl-10" [placeholder]="placeholder" [(ngModel)]="termo" autocomplete="off" />
          </label>
        </div>
        <ul class="flex-1 overflow-y-auto px-2 pb-3" role="listbox">
          <li *ngFor="let item of filtrados">
            <button type="button" role="option" (click)="escolher.emit(item)"
              class="w-full text-left flex items-center gap-3 rounded-md px-3 py-3 hover:bg-paper focus-visible:bg-paper">
              <span class="flex-1 min-w-0">
                <span class="block font-semibold truncate">{{ item.titulo }}</span>
                <span *ngIf="item.sub" class="block text-sm text-ink-soft truncate">{{ item.sub }}</span>
              </span>
              <span *ngIf="item.placa" class="plate shrink-0">{{ item.placa }}</span>
              <span *ngIf="item.tag" class="tag shrink-0" [ngClass]="item.tag.classe">{{ item.tag.label }}</span>
            </button>
          </li>
          <li *ngIf="!filtrados.length" class="px-3 py-8 text-center text-ink-soft">
            {{ itens.length ? 'Nada encontrado para “' + termo + '”.' : vazio }}
          </li>
        </ul>
      </div>
    </div>
  `
})
export class SeletorComponent implements OnChanges {
  @Input() aberto = false;
  @Input() titulo = 'Selecionar';
  @Input() placeholder = 'Pesquisar…';
  @Input() vazio = 'Nenhum item cadastrado.';
  @Input() itens: SeletorItem[] = [];
  @Output() escolher = new EventEmitter<SeletorItem>();
  @Output() fechar = new EventEmitter<void>();

  termo = '';

  ngOnChanges(): void {
    if (this.aberto) this.termo = '';
  }

  get filtrados(): SeletorItem[] {
    const q = this.termo.trim().toLowerCase();
    if (!q) return this.itens;
    return this.itens.filter(i =>
      [i.titulo, i.sub, i.placa].some(v => (v ?? '').toLowerCase().includes(q)));
  }

  @HostListener('document:keydown.escape')
  onEsc() { if (this.aberto) this.fechar.emit(); }
}
