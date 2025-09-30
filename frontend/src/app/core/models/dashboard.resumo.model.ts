import { MesValor } from "./mes.valor.model";
import { ProximaRevisao } from "./proxima.revisao.model";

export interface DashboardResumo {
  gastoMensal: MesValor[];
  litrosMensais: MesValor[];
  kmRodadoMensal: MesValor[];
  precoMedioLitroMensal: MesValor[];
  eficienciaMensal: MesValor[];
  revisoesPorMes: MesValor[];
  proximasRevisoes: ProximaRevisao[];
}