import { Carro } from "./carro.model";
import { User } from "./user.model";

export interface Revisao{
    idRevisao: number,
    kilometragemAtual: number,
    user: User,
    carro: Carro

}