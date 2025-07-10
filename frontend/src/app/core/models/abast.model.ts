import { Carro } from "./carro.model";
import { User } from "./user.model";

export interface Abast {
    id: number,
    litroComb: number,
    valorComb: number,
    kmAtual: number,
    user: User,
    dtAbast: Date,
    carro: Carro
}