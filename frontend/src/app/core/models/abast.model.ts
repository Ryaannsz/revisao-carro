import { Carro } from "./carro.model";
import { User } from "./user.model";

export interface Abast{
    idAbast: number,
    litroComb: number,
    valorComb: number,
    kmAtual: number,
    user: User,
    dtAbast: Date,
    carro: Carro
}