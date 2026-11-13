export interface ProximaRevisaoMaquina {
  maquinaId: number;
  identificacao: string | null;
  ultimaRevisao: string | null;
  horasRevisaoEstimada: number | null;
  horasAtual: number;
  proximaRevisaoEstimada: string | null;
  intervaloHoras: number;
}
