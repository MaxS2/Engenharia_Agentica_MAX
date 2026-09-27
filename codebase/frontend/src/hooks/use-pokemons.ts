import { keepPreviousData, useQuery } from '@tanstack/react-query';

import { fetchPokemonDetail, searchPokemons, type SearchParams } from '@/services/pokemons-service';

export function usePokemonSearch(params: SearchParams) {
  const { search = '', deckId, page = 0, size = 20 } = params;
  return useQuery({
    queryKey: ['pokemons', 'search', { search, deckId, page, size }],
    queryFn: () => searchPokemons({ search, deckId, page, size }),
    placeholderData: keepPreviousData,
    staleTime: 60_000,
  });
}

export function usePokemonDetail(id: number, deckId?: string) {
  return useQuery({
    queryKey: ['pokemons', 'detail', id, deckId ?? null],
    queryFn: () => fetchPokemonDetail(id, deckId),
    staleTime: 60_000,
    enabled: Number.isFinite(id) && id > 0,
  });
}
