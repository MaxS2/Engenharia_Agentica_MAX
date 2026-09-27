import { api } from './api';

import type { Deck, PokemonSummary } from '@/types/domain';

export interface PageResponse<T> {
  items: T[];
  page: number;
  size: number;
  totalItems: number;
  totalPages: number;
}

export async function listDecks(): Promise<Deck[]> {
  const { data } = await api.get<Deck[]>('/decks');
  return data;
}

export async function createDeck(name: string): Promise<Deck> {
  const { data } = await api.post<Deck>('/decks', { name });
  return data;
}

export async function deleteDeck(id: string): Promise<void> {
  await api.delete(`/decks/${id}`);
}

export async function listDeckPokemons(
  deckId: string,
  page: number,
  size = 4,
): Promise<PageResponse<PokemonSummary>> {
  const { data } = await api.get<PageResponse<PokemonSummary>>(
    `/decks/${deckId}/pokemons`,
    { params: { page, size } },
  );
  return data;
}

export async function addPokemonToDeck(deckId: string, pokemonId: number): Promise<void> {
  await api.post(`/decks/${deckId}/pokemons`, { pokemonId });
}
