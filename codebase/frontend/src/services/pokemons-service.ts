import { api } from './api';
import type { PageResponse } from './decks-service';

import type { PokemonDetail, PokemonSummary } from '@/types/domain';

export interface SearchParams {
  search?: string;
  deckId?: string;
  page?: number;
  size?: number;
}

export async function searchPokemons(params: SearchParams): Promise<PageResponse<PokemonSummary>> {
  const { data } = await api.get<PageResponse<PokemonSummary>>('/pokemons', {
    params: {
      search: params.search || undefined,
      deckId: params.deckId || undefined,
      page: params.page ?? 0,
      size: params.size ?? 20,
    },
  });
  return data;
}

export async function fetchPokemonDetail(id: number, deckId?: string): Promise<PokemonDetail> {
  const { data } = await api.get<PokemonDetail>(`/pokemons/${id}`, {
    params: { deckId: deckId || undefined },
  });
  return data;
}
