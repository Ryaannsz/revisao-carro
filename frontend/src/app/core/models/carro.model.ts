import { CarroStatus } from "./carro-status.enum";
import { Marca } from "./marca.model";
import { Modelo } from "./modelo.model";

export interface Carro {
  id: number,
  marca: Marca,
  modelo: Modelo,
  kmAdicionado: number,
  placa: string,
  dtAdicionado: number
  alarmeRevisaoKm: number,
  alarmeRevisaoMes: number,
  status: CarroStatus
}