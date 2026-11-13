import { ProximaRevisaoMaquina } from '../models/proxima.revisao.maquina.model';

export type SituacaoPreventiva = 'vencida' | 'proxima' | 'ok' | 'sem-dados';

/** % do intervalo de horas já consumido desde a última preventiva (0–100). */
export function progressoPreventiva(p: ProximaRevisaoMaquina | undefined | null): number {
  if (!p || p.horasRevisaoEstimada == null || !p.intervaloHoras) return 0;
  const inicio = p.horasRevisaoEstimada - p.intervaloHoras;
  const pct = ((p.horasAtual - inicio) / p.intervaloHoras) * 100;
  return Math.max(0, Math.min(100, pct));
}

/** Horas que faltam até a preventiva (negativo = atrasada). */
export function horasRestantes(p: ProximaRevisaoMaquina | undefined | null): number | null {
  if (!p || p.horasRevisaoEstimada == null) return null;
  return p.horasRevisaoEstimada - p.horasAtual;
}

/** Vence pelo que chegar primeiro: horas ou data. "Próxima" = 90% das horas ou 15 dias. */
export function situacaoPreventiva(p: ProximaRevisaoMaquina | undefined | null): SituacaoPreventiva {
  if (!p || (p.horasRevisaoEstimada == null && !p.proximaRevisaoEstimada)) return 'sem-dados';
  const restantes = horasRestantes(p);
  const dias = p.proximaRevisaoEstimada
    ? Math.round((new Date(p.proximaRevisaoEstimada + 'T00:00:00').getTime() - new Date().setHours(0, 0, 0, 0)) / 86400000)
    : null;
  if ((restantes != null && restantes <= 0) || (dias != null && dias < 0)) return 'vencida';
  if (progressoPreventiva(p) >= 90 || (dias != null && dias <= 15)) return 'proxima';
  return 'ok';
}

export const SITUACAO_INFO: Record<SituacaoPreventiva, { label: string; badge: string; barra: string }> = {
  'vencida':   { label: 'Preventiva vencida', badge: 'bg-red-100 text-red-700',     barra: 'from-red-500 to-red-600' },
  'proxima':   { label: 'Preventiva próxima', badge: 'bg-amber-100 text-amber-700', barra: 'from-amber-400 to-amber-500' },
  'ok':        { label: 'Em dia',             badge: 'bg-green-100 text-green-700', barra: 'from-emerald-500 to-emerald-600' },
  'sem-dados': { label: 'Sem dados',          badge: 'bg-gray-100 text-gray-600',   barra: 'from-gray-300 to-gray-400' },
};
