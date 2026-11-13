import { Marca } from "./marca.model";
import { Modelo } from "./modelo.model";
import { MaquinaStatus } from "./maquina-status.enum";

export interface Maquina {
    id: number,
    dtAdicionado: string,
    /** horímetro no cadastro */
    horas: number,
    marca: Marca | null,
    modelo: Modelo | null,
    chassi: string | null,
    identificacao?: string | null,
    tipo?: string | null,
    ano?: number | null,
    status?: MaquinaStatus | null,
    /** preventiva a cada N horas */
    alarmeRevisaoHoras?: number | null,
    /** preventiva a cada N meses */
    alarmeRevisaoMes?: number | null
}
