import { Component } from '@angular/core';
import { Carro } from '../../core/models/carro.model';
import { OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CarroService } from '../../core/services/carro.service';

@Component({
  selector: 'app-carro-unico',
  standalone: false,
  templateUrl: './carro-unico.component.html',
  styleUrl: './carro-unico.component.css'
})
export class CarroUnicoComponent implements OnInit {

  id!: number;
  carro: Carro = {idCarro: 0, marca: {idMarca: 0, marca: ""}, modelo: {idModelo: 0, modelo: ""}, kmAdicionado: 0, placa: "", dtAdicionado: 0}
  constructor(private route: ActivatedRoute, private carroService: CarroService) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.findCarro(this.id);  
  }

  findCarro(id: number){
    this.carroService.getWithId<Carro>(id).subscribe({
      next: (res) => {
        this.carro=res;
      }
    })
  }

}
