import { Component, Input } from '@angular/core';

/**
 * Leitura de odômetro / horímetro no formato de contador mecânico.
 * Zeros à esquerda ficam apagados; a casa decimal (horímetro) fica destacada,
 * como o último tambor de um contador de verdade.
 */
@Component({
  selector: 'app-medidor',
  standalone: false,
  template: `
    <div class="inline-flex items-end gap-2" [attr.aria-label]="ariaLabel" role="img">
      <div class="inline-flex rounded-md bg-ink p-[3px] gap-[2px] shadow-[inset_0_1px_0_rgba(255,255,255,.06)]">
        <span *ngFor="let d of inteiros; let i = index"
              class="cell" [class.lg]="size === 'lg'" [class.sm]="size === 'sm'" [class.dim]="i < zerosEsquerda">
          <span class="inline-block" [class.animate-roll]="animar" [style.animation-delay.ms]="i * 45">{{ d }}</span>
        </span>
        <span *ngIf="decimais" class="cell dec" [class.lg]="size === 'lg'" [class.sm]="size === 'sm'">
          <span class="inline-block" [class.animate-roll]="animar" [style.animation-delay.ms]="inteiros.length * 45">{{ decimal }}</span>
        </span>
      </div>
      <span class="font-cond font-semibold text-ink-soft leading-none"
            [class.text-2xl]="size === 'lg'" [class.text-lg]="size === 'md'" [class.text-sm]="size === 'sm'">{{ unidade }}</span>
    </div>
  `,
  styles: [`
    .cell {
      display: inline-flex; align-items: center; justify-content: center; overflow: hidden;
      width: 1.45rem; height: 2rem; border-radius: 3px;
      background: linear-gradient(#2E3F39, #22302B 45%, #1B2723 55%, #2E3F39);
      color: #F2F3EF; font-family: 'Barlow Condensed', Barlow, sans-serif; font-weight: 600;
      font-size: 1.4rem; line-height: 1; font-variant-numeric: tabular-nums;
    }
    .cell.lg { width: 2.1rem; height: 3rem; font-size: 2.15rem; }
    .cell.sm { width: 1.05rem; height: 1.5rem; font-size: 1.05rem; border-radius: 2px; }
    .cell.dim { color: #6E7F78; }
    .cell.dec { background: #F5B800; color: #22302B; }
    @media (max-width: 380px) { .cell.lg { width: 1.75rem; height: 2.6rem; font-size: 1.8rem; } }
  `]
})
export class MedidorComponent {
  @Input() valor: number | null | undefined = 0;
  @Input() unidade = 'km';
  /** Mínimo de casas inteiras exibidas (completa com zeros). */
  @Input() digitos = 6;
  /** 0 ou 1 casa decimal (horímetro usa 1). */
  @Input() decimais: 0 | 1 = 0;
  @Input() size: 'sm' | 'md' | 'lg' = 'md';
  @Input() animar = false;

  get inteiros(): string[] {
    const n = Math.max(0, Math.floor(Number(this.valor) || 0));
    return String(n).padStart(this.digitos, '0').split('');
  }

  get zerosEsquerda(): number {
    const n = Math.max(0, Math.floor(Number(this.valor) || 0));
    return Math.max(0, this.inteiros.length - String(n).length);
  }

  get decimal(): string {
    const v = Math.max(0, Number(this.valor) || 0);
    return String(Math.floor((v * 10) % 10));
  }

  get ariaLabel(): string {
    const v = Number(this.valor) || 0;
    return `${v.toLocaleString('pt-BR', { maximumFractionDigits: this.decimais })} ${this.unidade}`;
  }
}
