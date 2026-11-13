import { Carro } from '../models/carro.model';
import { ProximaRevisao } from '../models/proxima.revisao.model';

export type SituacaoPreventiva = 'vencida' | 'proxima' | 'ok' | 'sem-dados';

/** Classes de etiqueta (usar com "tag") e de barra de progresso para cada situação. */
export const SITUACAO_INFO: Record<SituacaoPreventiva, { label: string; tag: string; barra: string }> = {
  'vencida':   { label: 'Revisão vencida', tag: 'tag-over', barra: 'bg-over' },
  'proxima':   { label: 'Revisão próxima', tag: 'tag-due',  barra: 'bg-due' },
  'ok':        { label: 'Em dia',          tag: 'tag-ok',   barra: 'bg-ok' },
  'sem-dados': { label: 'Sem dados',       tag: 'tag-idle', barra: 'bg-line-strong' },
};

/** Dias até uma data "yyyy-MM-dd" (negativo = passou). */
export function diasAte(data: string | null | undefined): number | null {
  if (!data) return null;
  const alvo = new Date(String(data).slice(0, 10) + 'T00:00:00').getTime();
  if (isNaN(alvo)) return null;
  return Math.round((alvo - new Date().setHours(0, 0, 0, 0)) / 86400000);
}

/**
 * Regra única para carro e máquina: vence pelo que chegar primeiro (medidor ou data).
 * "Próxima" = 90% do intervalo do medidor ou 15 dias para a data.
 */
export function avaliarSituacao(restante: number | null, progresso: number, data: string | null | undefined): SituacaoPreventiva {
  const dias = diasAte(data);
  if (restante == null && dias == null) return 'sem-dados';
  if ((restante != null && restante <= 0) || (dias != null && dias < 0)) return 'vencida';
  if (progresso >= 90 || (dias != null && dias <= 15)) return 'proxima';
  return 'ok';
}

/* ---------------- Carros ---------------- */

const KM_PADRAO = 10000;

export function intervaloKm(carro: Carro | null | undefined): number {
  return carro?.alarmeRevisaoKm && carro.alarmeRevisaoKm > 0 ? carro.alarmeRevisaoKm : KM_PADRAO;
}

export function progressoRevisaoCarro(carro: Carro | null | undefined, p: ProximaRevisao | null | undefined, kmAtual?: number | null): number {
  if (!carro || !p || p.kmRevisaoEstimado == null) return 0;
  const intervalo = intervaloKm(carro);
  const atual = kmAtual ?? p.kmAtual ?? 0;
  const pct = ((atual - (p.kmRevisaoEstimado - intervalo)) / intervalo) * 100;
  return Math.max(0, Math.min(100, pct));
}

export function kmRestantes(p: ProximaRevisao | null | undefined, kmAtual?: number | null): number | null {
  if (!p || p.kmRevisaoEstimado == null) return null;
  return p.kmRevisaoEstimado - (kmAtual ?? p.kmAtual ?? 0);
}

export function situacaoCarro(carro: Carro | null | undefined, p: ProximaRevisao | null | undefined, kmAtual?: number | null): SituacaoPreventiva {
  if (!p) return 'sem-dados';
  return avaliarSituacao(kmRestantes(p, kmAtual), progressoRevisaoCarro(carro, p, kmAtual), p.proximaRevisaoEstimada);
}

/** "Fiat Uno" a partir de marca/modelo (vêm em minúsculo do backend). */
export function nomeVeiculo(marca?: string | null, modelo?: string | null): string {
  const cap = (s?: string | null) => (s ?? '').trim().replace(/(^|\s)\p{L}/gu, c => c.toUpperCase());
  return [cap(marca), cap(modelo)].filter(Boolean).join(' ') || 'Sem marca/modelo';
}

/**
 * Texto curto do prazo, sempre explicando o motivo quando venceu:
 * "Revisão em 3.083 km", "Passou 60 h", "Venceu pela data".
 */
export function textoPrazo(restante: number | null, dias: number | null, unidade: 'km' | 'h', nome = 'Revisão'): string {
  const fmt = (n: number) => Math.abs(n).toLocaleString('pt-BR', { maximumFractionDigits: unidade === 'h' ? 1 : 0 });
  if (restante != null && restante <= 0) return `${nome} passou ${fmt(restante)} ${unidade}`;
  if (dias != null && dias < 0) return `${nome} venceu pela data`;
  if (restante != null) return `${nome} em ${fmt(restante)} ${unidade}`;
  return `Sem previsão de ${nome.toLowerCase()}`;
}
