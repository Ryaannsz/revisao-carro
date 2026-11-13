export enum MaquinaStatus {
  DISPONIVEL = 'DISPONIVEL',
  EM_OBRA = 'EM_OBRA',
  MANUTENCAO = 'MANUTENCAO',
  PARADA = 'PARADA',
}

/** Rótulo e classe de etiqueta (usar com "tag") de cada status (máquina sem status = disponível). */
export const MAQUINA_STATUS_INFO: Record<MaquinaStatus, { label: string; tag: string }> = {
  [MaquinaStatus.DISPONIVEL]: { label: 'Disponível', tag: 'tag-ok' },
  [MaquinaStatus.EM_OBRA]: { label: 'Em obra', tag: 'tag-info' },
  [MaquinaStatus.MANUTENCAO]: { label: 'Manutenção', tag: 'tag-due' },
  [MaquinaStatus.PARADA]: { label: 'Parada', tag: 'tag-idle' },
};

export const TIPOS_MAQUINA = [
  'Retroescavadeira', 'Escavadeira', 'Pá carregadeira', 'Trator', 'Motoniveladora',
  'Rolo compactador', 'Minicarregadeira', 'Caminhão', 'Empilhadeira', 'Gerador', 'Outro',
];
