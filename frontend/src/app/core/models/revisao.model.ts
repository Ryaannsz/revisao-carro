import { Carro } from "./carro.model";
import { User } from "./user.model";

export interface Revisao {
    id: number,
    kmAtual: number,
    user: User,
    carro: Carro,
    dtRevisao: Date

}