import { Maquina } from "./maquina.model";
import { User } from "./user.model";

export interface RevisaoMaquina {
    id: number,
    dtRevisao: string,
    horasAtual: number,
    valor?: number | null,
    descricao?: string | null,
    user: User | null,
    maquina: Maquina
}
