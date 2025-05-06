import { Component } from '@angular/core';
import { Carro } from '../../core/models/carro.model';
import { OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-carro-unico',
  standalone: false,
  templateUrl: './carro-unico.component.html',
  styleUrl: './carro-unico.component.css'
})
export class CarroUnicoComponent implements OnInit {

  id!: number;

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    console.log('ID recebido:', this.id);
    

  }

}
