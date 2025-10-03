import { MesValor } from "./mes.valor.model";

export interface SeriesBlock {
  sum: MesValor[];                     
  byCar: Record<number, MesValor[]>;  
}