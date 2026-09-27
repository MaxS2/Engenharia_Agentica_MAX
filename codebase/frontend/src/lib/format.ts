/**
 * Capitaliza a primeira letra (usado para nomes de pokemon vindos em minusculo da PokeAPI).
 */
export function capitalize(value: string): string {
  if (!value) return value;
  return value.charAt(0).toUpperCase() + value.slice(1);
}

/**
 * Formata ID do pokemon no padrao PokeDex: #025.
 */
export function formatPokemonId(id: number): string {
  return `#${String(id).padStart(3, '0')}`;
}
