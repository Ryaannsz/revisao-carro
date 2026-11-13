export interface GrupoMes<T> {
  chave: string;   // yyyy-MM
  titulo: string;  // "novembro de 2026"
  total: number;
  itens: T[];
}

const MESES = ['janeiro', 'fevereiro', 'março', 'abril', 'maio', 'junho', 'julho', 'agosto', 'setembro', 'outubro', 'novembro', 'dezembro'];

/** Agrupa por mês (mais recente primeiro), somando `valor` de cada item. */
export function agruparPorMes<T>(itens: T[], data: (i: T) => string | Date | null | undefined, valor: (i: T) => number): GrupoMes<T>[] {
  const mapa = new Map<string, GrupoMes<T>>();
  for (const i of itens) {
    const d = String(data(i) ?? '').slice(0, 7) || 'sem-data';
    let g = mapa.get(d);
    if (!g) {
      const [ano, mes] = d.split('-').map(Number);
      g = { chave: d, titulo: ano && mes ? `${MESES[mes - 1]} de ${ano}` : 'Sem data', total: 0, itens: [] };
      mapa.set(d, g);
    }
    g.itens.push(i);
    g.total += valor(i) || 0;
  }
  return [...mapa.values()].sort((a, b) => b.chave.localeCompare(a.chave));
}
