import { Marca } from "./marca.model";
import { Modelo } from "./modelo.model";

export interface Carro {
    idCarro: number,
    marca: Marca,
    modelo: Modelo,
    kilometragem: number,
    placa: string,
    dtAdicionado: number
  }