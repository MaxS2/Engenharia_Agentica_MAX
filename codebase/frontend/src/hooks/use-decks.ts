import { keepPreviousData, useMutation, useQuery, useQueryClient } from '@tanstack/react-query';

import {
  addPokemonToDeck,
  createDeck,
  deleteDeck,
  listDeckPokemons,
  listDecks,
} from '@/services/decks-service';

export const decksQueryKey = ['decks'] as const;
export const deckPokemonsQueryKey = (deckId: string, page: number, size: number) =>
  ['decks', deckId, 'pokemons', { page, size }] as const;

export function useDecks(enabled = true) {
  return useQuery({
    queryKey: decksQueryKey,
    queryFn: listDecks,
    enabled,
  });
}

export function useCreateDeck() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (name: string) => createDeck(name),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: decksQueryKey });
    },
  });
}

export function useDeleteDeck() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (deckId: string) => deleteDeck(deckId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: decksQueryKey });
    },
  });
}

export function useDeckPokemons(deckId: string | null, page: number, size = 4) {
  return useQuery({
    queryKey: deckId ? deckPokemonsQueryKey(deckId, page, size) : ['deck-pokemons', 'disabled'],
    queryFn: () => listDeckPokemons(deckId as string, page, size),
    enabled: !!deckId,
    placeholderData: keepPreviousData,
  });
}

export function useAddPokemonToDeck(deckId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (pokemonId: number) => addPokemonToDeck(deckId, pokemonId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: decksQueryKey });
      queryClient.invalidateQueries({ queryKey: ['decks', deckId, 'pokemons'] });
    },
  });
}
