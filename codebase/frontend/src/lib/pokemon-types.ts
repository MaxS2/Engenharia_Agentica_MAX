/**
 * Mapeamento dos 18 tipos de Pokemon para rotulo PT-BR e cor do badge.
 * Referencia: layout-details.md §2.2 "Selo visual com cores correspondentes".
 */
export interface PokemonTypeStyle {
  label: string;
  bg: string;
  text: string;
}

const TYPE_STYLES: Record<string, PokemonTypeStyle> = {
  Normal: { label: 'Normal', bg: 'bg-zinc-400', text: 'text-white' },
  Fogo: { label: 'Fogo', bg: 'bg-red-500', text: 'text-white' },
  Agua: { label: 'Agua', bg: 'bg-blue-500', text: 'text-white' },
  Eletrico: { label: 'Eletrico', bg: 'bg-yellow-400', text: 'text-zinc-900' },
  Grama: { label: 'Grama', bg: 'bg-green-500', text: 'text-white' },
  Gelo: { label: 'Gelo', bg: 'bg-cyan-300', text: 'text-zinc-900' },
  Lutador: { label: 'Lutador', bg: 'bg-orange-700', text: 'text-white' },
  Venenoso: { label: 'Venenoso', bg: 'bg-purple-500', text: 'text-white' },
  Terrestre: { label: 'Terrestre', bg: 'bg-amber-600', text: 'text-white' },
  Voador: { label: 'Voador', bg: 'bg-sky-400', text: 'text-white' },
  Psiquico: { label: 'Psiquico', bg: 'bg-pink-500', text: 'text-white' },
  Inseto: { label: 'Inseto', bg: 'bg-lime-500', text: 'text-zinc-900' },
  Pedra: { label: 'Pedra', bg: 'bg-stone-500', text: 'text-white' },
  Fantasma: { label: 'Fantasma', bg: 'bg-indigo-700', text: 'text-white' },
  Dragao: { label: 'Dragao', bg: 'bg-violet-700', text: 'text-white' },
  Sombrio: { label: 'Sombrio', bg: 'bg-zinc-800', text: 'text-white' },
  Metalico: { label: 'Metalico', bg: 'bg-slate-400', text: 'text-white' },
  Fada: { label: 'Fada', bg: 'bg-pink-300', text: 'text-zinc-900' },
};

export function getTypeStyle(type: string): PokemonTypeStyle {
  return TYPE_STYLES[type] ?? { label: type, bg: 'bg-zinc-300', text: 'text-zinc-900' };
}
