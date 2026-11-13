import { ProximaRevisaoMaquina } from '../models/proxima.revisao.maquina.model';
import { Maquina } from '../models/maquina.model';
import { SituacaoPreventiva, avaliarSituacao, nomeVeiculo } from './situacao.utils';

export type { SituacaoPreventiva } from './situacao.utils';
export { SITUACAO_INFO } from './situacao.utils';

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
  if (!p) return 'sem-dados';
  return avaliarSituacao(horasRestantes(p), progressoPreventiva(p), p.proximaRevisaoEstimada);
}

/** Nome principal da máquina: a identificação da frota ou, na falta, tipo + marca/modelo. */
export function nomeMaquina(m: Maquina | null | undefined): string {
  if (!m) return '';
  if (m.identificacao?.trim()) return m.identificacao.trim();
  return [m.tipo, nomeVeiculo(m.marca?.marca, m.modelo?.modelo)]
    .filter(s => s && s !== 'Sem marca/modelo').join(' ') || `Máquina #${m.id}`;
}

/** Linha secundária: tipo, marca/modelo e ano (o que existir e não estiver no nome). */
export function detalheMaquina(m: Maquina | null | undefined): string {
  if (!m) return '';
  const mm = nomeVeiculo(m.marca?.marca, m.modelo?.modelo);
  const partes = m.identificacao?.trim()
    ? [m.tipo, mm !== 'Sem marca/modelo' ? mm : null, m.ano ? String(m.ano) : null]
    : [m.ano ? String(m.ano) : null, m.chassi ? `Série ${m.chassi}` : null];
  return partes.filter(Boolean).join(', ');
}
