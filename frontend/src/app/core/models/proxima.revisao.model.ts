export interface ProximaRevisao {
    carroId: number;
    placa: string;
    ultimaRevisao: string | null;
    kmRevisaoEstimado: number | null;
    kmAtual: number,
    proximaRevisaoEstimada: string | null;


}