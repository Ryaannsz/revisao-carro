import { Carro } from "./carro.model";
import { User } from "./user.model";

export interface Abast {
    id: number,
    litroComb: number,
    /** valor TOTAL pago (não é preço por litro) */
    valorComb: number,
    kmAtual: number,
    user: User | null,
    dtAbast: Date,
    carro: Carro
}