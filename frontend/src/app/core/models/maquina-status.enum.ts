export enum MaquinaStatus {
  DISPONIVEL = 'DISPONIVEL',
  EM_OBRA = 'EM_OBRA',
  MANUTENCAO = 'MANUTENCAO',
  PARADA = 'PARADA',
}

/** Rótulo e classes Tailwind de cada status (máquina sem status = disponível). */
export const MAQUINA_STATUS_INFO: Record<MaquinaStatus, { label: string; badge: string }> = {
  [MaquinaStatus.DISPONIVEL]: { label: 'Disponível', badge: 'bg-green-100 text-green-700' },
  [MaquinaStatus.EM_OBRA]: { label: 'Em obra', badge: 'bg-blue-100 text-blue-700' },
  [MaquinaStatus.MANUTENCAO]: { label: 'Manutenção', badge: 'bg-amber-100 text-amber-700' },
  [MaquinaStatus.PARADA]: { label: 'Parada', badge: 'bg-gray-200 text-gray-700' },
};

export const TIPOS_MAQUINA = [
  'Retroescavadeira', 'Escavadeira', 'Pá carregadeira', 'Trator', 'Motoniveladora',
  'Rolo compactador', 'Minicarregadeira', 'Caminhão', 'Empilhadeira', 'Gerador', 'Outro',
];
