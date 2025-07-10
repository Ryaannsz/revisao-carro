import { Marca } from "./marca.model";
import { Modelo } from "./modelo.model";

export interface RevisaoMaquina {
    id: number,
    dtAdicionado: Date,
    horas: number,
    marca: Marca,
    modelo: Modelo,
    chassi: string
}