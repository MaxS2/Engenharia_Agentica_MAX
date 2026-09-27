'use client';

import { useEffect, useMemo, useState } from 'react';
import { AxiosError } from 'axios';
import { ChevronLeft, ChevronRight, Search } from 'lucide-react';
import { toast } from 'sonner';

import { AppSidebar } from '@/components/app-sidebar';
import { CatalogPokemonCard } from '@/components/catalog-pokemon-card';
import { PokemonCardSkeleton } from '@/components/pokemon-card-skeleton';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { useAuth } from '@/contexts/auth-context';
import { useAddPokemonToDeck, useDecks } from '@/hooks/use-decks';
import { useDebouncedValue } from '@/hooks/use-debounced-value';
import { usePokemonSearch } from '@/hooks/use-pokemons';

const PAGE_SIZE = 20;

export default function CatalogPage() {
  const { user } = useAuth();
  const { data: decks = [] } = useDecks();
  const [selectedDeckId, setSelectedDeckId] = useState<string>('');
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const debouncedSearch = useDebouncedValue(search, 300);

  useEffect(() => {
    if (!selectedDeckId && decks.length > 0) {
      setSelectedDeckId(decks[0].id);
    }
  }, [decks, selectedDeckId]);

  useEffect(() => {
    setPage(0);
  }, [debouncedSearch, selectedDeckId]);

  const query = usePokemonSearch({
    search: debouncedSearch,
    deckId: selectedDeckId || undefined,
    page,
    size: PAGE_SIZE,
  });

  const addMutation = useAddPokemonToDeck(selectedDeckId);
  const items = query.data?.items ?? [];
  const totalPages = query.data?.totalPages ?? 0;
  const [busyId, setBusyId] = useState<number | null>(null);

  const handleAdd = async (pokemonId: number) => {
    if (!selectedDeckId) {
      toast.warning('Selecione um deck de destino antes de adicionar.');
      return;
    }
    setBusyId(pokemonId);
    try {
      await addMutation.mutateAsync(pokemonId);
      toast.success('Pokémon adicionado ao deck.');
    } catch (err) {
      if (err instanceof AxiosError && err.response?.status === 409) {
        toast.error('Esse pokémon já está nesse deck.');
      } else {
        toast.error('Não foi possível adicionar. Tente novamente.');
      }
    } finally {
      setBusyId(null);
    }
  };

  const deckOptions = useMemo(() => decks, [decks]);

  if (!user) return null;

  return (
    <>
      <AppSidebar
        user={user}
        decks={decks}
        selectedDeckId={selectedDeckId}
        onSelectDeck={setSelectedDeckId}
      />

      <main className="flex-1 overflow-y-auto">
        <header className="border-b bg-white/60 px-6 py-5 backdrop-blur">
          <div>
            <p className="text-xs uppercase tracking-wider text-muted-foreground">Catálogo</p>
            <h1 className="text-2xl font-semibold text-primary">Explorar Pokémons</h1>
          </div>
          <div className="mt-4 grid items-end gap-3 md:grid-cols-[1fr_260px]">
            <div className="space-y-1">
              <Label htmlFor="pokemon-search" className="text-xs uppercase tracking-wider text-muted-foreground">
                Buscar
              </Label>
              <div className="relative">
                <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                <Input
                  id="pokemon-search"
                  placeholder="Buscar por nome (ex.: pikachu)"
                  value={search}
                  onChange={(e) => setSearch(e.target.value)}
                  className="pl-9"
                />
              </div>
            </div>
            <div className="space-y-1">
              <Label htmlFor="deck-target" className="text-xs uppercase tracking-wider text-muted-foreground">
                Deck de destino
              </Label>
              <Select value={selectedDeckId} onValueChange={setSelectedDeckId}>
                <SelectTrigger id="deck-target">
                  <SelectValue placeholder="Selecione um deck" />
                </SelectTrigger>
                <SelectContent>
                  {deckOptions.length === 0 ? (
                    <SelectItem value="__empty" disabled>
                      Nenhum deck disponível
                    </SelectItem>
                  ) : (
                    deckOptions.map((deck) => (
                      <SelectItem key={deck.id} value={deck.id}>
                        {deck.name} ({deck.pokemonCount})
                      </SelectItem>
                    ))
                  )}
                </SelectContent>
              </Select>
            </div>
          </div>
        </header>

        <section className="p-6">
          {query.error && (
            <p className="mb-4 rounded-md bg-destructive/10 px-3 py-2 text-sm text-destructive">
              Falha ao carregar o catálogo. Tente novamente em instantes.
            </p>
          )}

          {!query.isLoading && items.length === 0 ? (
            <div className="rounded-lg border border-dashed py-16 text-center text-muted-foreground">
              Nenhum pokémon encontrado para &quot;{debouncedSearch}&quot;.
            </div>
          ) : (
            <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
              {query.isLoading
                ? Array.from({ length: 8 }).map((_, i) => <PokemonCardSkeleton key={i} />)
                : items.map((pokemon) => (
                    <CatalogPokemonCard
                      key={pokemon.id}
                      pokemon={pokemon}
                      adding={busyId === pokemon.id}
                      disabled={!selectedDeckId}
                      onAdd={() => handleAdd(pokemon.id)}
                    />
                  ))}
            </div>
          )}

          {totalPages > 1 && (
            <div className="mt-6 flex items-center justify-center gap-3">
              <Button
                variant="outline"
                size="sm"
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                disabled={page === 0}
                className="gap-1"
              >
                <ChevronLeft className="h-4 w-4" /> Anterior
              </Button>
              <span className="text-sm text-muted-foreground">
                Página {page + 1} de {totalPages}
              </span>
              <Button
                variant="outline"
                size="sm"
                onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                disabled={page >= totalPages - 1}
                className="gap-1"
              >
                Próximo <ChevronRight className="h-4 w-4" />
              </Button>
            </div>
          )}
        </section>
      </main>
    </>
  );
}
