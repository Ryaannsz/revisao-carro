import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CarroService } from '../../core/services/carro.service';
import { AbastService } from '../../core/services/abast.service';
import { RevisaoService } from '../../core/services/revisao.service';
import { Carro } from '../../core/models/carro.model';
import { Abast } from '../../core/models/abast.model';
import { Revisao } from '../../core/models/revisao.model';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-carro-unico',
  standalone: false,
  templateUrl: './carro-unico.component.html',
  styleUrl: './carro-unico.component.css'
})
export class CarroUnicoComponent implements OnInit {

  carro: Carro | null = null;
  abastecimentos: Abast[] = [];
  revisoes: Revisao[] = [];
  expAbastId: number | null = null;
  expRevisaoId: number | null = null;
  id!: number;
  loading: boolean = false;


  constructor(private route: ActivatedRoute,
    private carroService: CarroService,
    private abastService: AbastService,
    private revisaoService: RevisaoService,
    private toastService: ToastService,
  ) { }

  ngOnInit(): void {
    this.loading = true;
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.carroService.getWithId<Carro>(this.id).subscribe(c => { this.carro = c; this.getKm() });
    this.abastService.getListWithId<Abast>(this.id, '/carro').subscribe(a => this.abastecimentos = a);
    this.revisaoService.getListWithId<Revisao>(this.id, '/carro').subscribe(r => { this.revisoes = r; this.loading = false });
  }

  toggleAbast(id: number) { this.expAbastId = this.expAbastId === id ? null : id; }
  toggleRevisao(id: number) { this.expRevisaoId = this.expRevisaoId === id ? null : id; }

  getKm() {
    this.carroService.getKmRecente(this.carro?.idCarro ?? 0).subscribe({
      next: (res) => {
        if (this.carro) {
          this.toastService.showSuccess("Carro carregado com sucesso!");
          this.carro.kmAdicionado = res;
        }
      }
    });
  }


}
