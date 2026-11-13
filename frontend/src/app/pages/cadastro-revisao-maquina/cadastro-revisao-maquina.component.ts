import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Maquina } from '../../core/models/maquina.model';
import { MAQUINA_STATUS_INFO, MaquinaStatus } from '../../core/models/maquina-status.enum';
import { MaquinaService } from '../../core/services/maquina.service';
import { RevisaoMaquinaService } from '../../core/services/maquina.revisao.service';
import { ToastService } from '../../core/services/toast.service';
import { SeletorItem } from '../../shared/seletor/seletor.component';
import { detalheMaquina, nomeMaquina } from '../../core/utils/maquina.utils';
import { hojeISO } from '../../core/utils/data.utils';

@Component({
  selector: 'app-cadastro-revisao-maquina',
  standalone: false,
  templateUrl: './cadastro-revisao-maquina.component.html'
})
export class CadastroRevisaoMaquinaComponent implements OnInit {
  form: FormGroup;
  maquinas: Maquina[] = [];
  selecionada: Maquina | null = null;
  horasAtual: number | null = null;
  seletorAberto = false;
  loading = false;
  enviado = false;
  liberarMaquina = true;
  ultimaLancada: Maquina | null = null;
  readonly hoje = hojeISO();

  /** Serviços típicos de máquina pesada, para lançar rápido pelo celular. */
  readonly sugestoes = [
    'Troca de óleo do motor e filtro', 'Filtro de combustível', 'Separador de água', 'Filtro de ar',
    'Óleo hidráulico', 'Filtro hidráulico', 'Óleo de transmissão', 'Graxa nos pinos', 'Líquido de arrefecimento',
  ];

  constructor(
    private fb: FormBuilder,
    private maquinaService: MaquinaService,
    private revisaoService: RevisaoMaquinaService,
    private toastService: ToastService,
    private route: ActivatedRoute
  ) {
    this.form = this.fb.group({
      maquina: [null, Validators.required],
      horasAtual: [null, [Validators.required, Validators.min(0)]],
      dtRevisao: [this.hoje],
      valor: [null, [Validators.min(0)]],
      descricao: ['', [Validators.maxLength(1000)]],
    });
  }

  ngOnInit(): void {
    this.maquinaService.listar().subscribe({
      next: (lista) => {
        this.maquinas = lista;
        const id = Number(this.route.snapshot.queryParamMap.get('maquina'));
        const m = id ? lista.find(x => x.id === id) : null;
        if (m) this.selecionar(m);
      },
      error: () => this.toastService.showError('Não foi possível carregar as máquinas.')
    });
  }

  get itens(): SeletorItem[] {
    return this.maquinas.map(m => {
      const s = m.status ?? MaquinaStatus.DISPONIVEL;
      return {
        id: m.id, titulo: nomeMaquina(m), sub: detalheMaquina(m),
        tag: { label: MAQUINA_STATUS_INFO[s].label, classe: MAQUINA_STATUS_INFO[s].tag },
      };
    });
  }

  nome(m: Maquina | null) { return nomeMaquina(m); }
  detalhe(m: Maquina | null) { return detalheMaquina(m); }

  get emManutencao(): boolean { return this.selecionada?.status === MaquinaStatus.MANUTENCAO; }

  erro(campo: string): boolean {
    const c = this.form.get(campo);
    return !!c && c.invalid && (c.touched || this.enviado);
  }

  get horasMenor(): boolean {
    const h = this.form.value.horasAtual;
    return h !== null && h !== '' && this.horasAtual != null && Number(h) < this.horasAtual;
  }

  escolher(item: SeletorItem) {
    const m = this.maquinas.find(x => x.id === item.id);
    if (m) this.selecionar(m);
  }

  selecionar(m: Maquina) {
    this.selecionada = m;
    this.form.patchValue({ maquina: m });
    this.seletorAberto = false;
    this.horasAtual = null;
    this.maquinaService.horasRecentes(m.id).subscribe(h => this.horasAtual = h);
  }

  adicionarSugestao(s: string) {
    const atual: string = (this.form.value.descricao ?? '').trim();
    if (atual.toLowerCase().includes(s.toLowerCase())) return;
    this.form.patchValue({ descricao: atual ? `${atual}, ${s.toLowerCase()}` : s });
  }

  salvar() {
    this.enviado = true;
    this.form.markAllAsTouched();
    if (this.form.invalid) {
      this.toastService.showWarning(this.selecionada ? 'Confira os campos destacados.' : 'Escolha a máquina.');
      return;
    }
    if (this.horasMenor) {
      this.toastService.showWarning('O horímetro informado é menor que o último registrado.');
      return;
    }
    const v = this.form.value;
    const m = this.selecionada!;
    const liberar = this.emManutencao && this.liberarMaquina;
    this.loading = true;
    this.revisaoService.registrar({
      maquina: { id: m.id },
      horasAtual: Number(v.horasAtual),
      dtRevisao: v.dtRevisao || null,
      valor: v.valor !== null && v.valor !== '' ? Number(v.valor) : null,
      descricao: v.descricao?.trim() || null,
    }).subscribe({
      next: () => {
        const concluir = () => {
          this.loading = false;
          this.toastService.showSuccess(liberar ? 'Manutenção registrada e máquina liberada.' : 'Manutenção registrada.');
          this.ultimaLancada = m;
          this.enviado = false;
          this.form.reset({ maquina: null, dtRevisao: this.hoje, descricao: '' });
          this.selecionada = null;
          this.horasAtual = null;
        };
        if (liberar) {
          this.maquinaService.atualizarStatus(m.id, MaquinaStatus.DISPONIVEL).subscribe({
            next: () => { m.status = MaquinaStatus.DISPONIVEL; concluir(); },
            error: () => { concluir(); this.toastService.showWarning('A manutenção foi salva, mas a máquina continua como em manutenção.'); }
          });
        } else concluir();
      },
      error: (err) => {
        this.loading = false;
        this.toastService.showError(typeof err?.error === 'string' && err.error ? err.error : 'Não foi possível registrar a manutenção.');
      }
    });
  }
}
