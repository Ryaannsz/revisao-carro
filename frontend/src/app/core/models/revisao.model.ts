import { Carro } from "./carro.model";
import { User } from "./user.model";

export interface Revisao {
    id: number,
    kmAtual: number,
    /** custo total (peças + mão de obra) */
    valor?: number | null,
    descricao?: string | null,
    user: User | null,
    carro: Carro,
    dtRevisao: Date
}
