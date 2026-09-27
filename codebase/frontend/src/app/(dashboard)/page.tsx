'use client';

import { Suspense, useEffect, useMemo, useState } from 'react';
import { useSearchParams } from 'next/navigation';
import { AxiosError } from 'axios';
import { ChevronLeft, ChevronRight, Sparkles, Trash2 } from 'lucide-react';
import { toast } from 'sonner';

import { AppSidebar } from '@/components/app-sidebar';
import { CreateDeckDialog } from '@/components/create-deck-dialog';
import { GiftModal } from '@/components/gift-modal';
import { PokemonCard } from '@/components/pokemon-card';
import { PokemonCardSkeleton } from '@/components/pokemon-card-skeleton';
import { Button } from '@/components/ui/button';
import { useAuth } from '@/contexts/auth-context';
import { useDeckPokemons, useDecks, useDeleteDeck } from '@/hooks/use-decks';
import { useAcceptGift, usePendingGifts, useRejectGift } from '@/hooks/use-gifts';

const PAGE_SIZE = 4;

export default function DashboardPage() {
  return (
    <Suspense fallback={null}>
      <DashboardContent />
    </Suspense>
  );
}

function DashboardContent() {
  const { user } = useAuth();
  const searchParams = useSearchParams();
  const deckQueryParam = searchParams.get('deck');
  const { data: decks = [], isLoading: isLoadingDecks, error: decksError } = useDecks();
  const { data: pendingGifts = [] } = usePendingGifts(!!user);
  const acceptGift = useAcceptGift();
  const rejectGift = useRejectGift();

  const [selectedDeckId, setSelectedDeckId] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [createOpen, setCreateOpen] = useState(false);
  const [giftOpen, setGiftOpen] = useState(false);
  const [dismissedGiftIds, setDismissedGiftIds] = useState<string[]>([]);

  const deleteDeck = useDeleteDeck();
  const pokemonsQuery = useDeckPokemons(selectedDeckId, page, PAGE_SIZE);

  const currentGift = useMemo(
    () => pendingGifts.find((g) => !dismissedGiftIds.includes(g.id)) ?? null,
    [pendingGifts, dismissedGiftIds],
  );

  useEffect(() => {
    if (currentGift) setGiftOpen(true);
  }, [currentGift]);

  useEffect(() => {
    if (decks.length === 0) {
      setSelectedDeckId(null);
      return;
    }
    if (deckQueryParam && decks.some((d) => d.id === deckQueryParam) && selectedDeckId !== deckQueryParam) {
      setSelectedDeckId(deckQueryParam);
      setPage(0);
      return;
    }
    if (!selectedDeckId || !decks.some((d) => d.id === selectedDeckId)) {
      setSelectedDeckId(decks[0].id);
      setPage(0);
    }
  }, [decks, selectedDeckId, deckQueryParam]);

  const selectedDeck = decks.find((d) => d.id === selectedDeckId) ?? null;
  const totalPages = pokemonsQuery.data?.totalPages ?? 0;
  const items = pokemonsQuery.data?.items ?? [];
  const isEmpty = !pokemonsQuery.isLoading && items.length === 0;

  const handleSelectDeck = (deckId: string) => {
    setSelectedDeckId(deckId);
    setPage(0);
  };

  const handleDelete = async () => {
    if (!selectedDeck) return;
    if (!confirm(`Remover o deck "${selectedDeck.name}"? Essa ação é irreversível.`)) return;
    try {
      await deleteDeck.mutateAsync(selectedDeck.id);
      toast.success(`Deck "${selectedDeck.name}" removido.`);
      setSelectedDeckId(null);
      setPage(0);
    } catch {
      toast.error('Não foi possível remover o deck.');
    }
  };

  const handleAcceptGift = async (giftId: string, deckId: string) => {
    try {
      await acceptGift.mutateAsync({ giftId, deckId });
      toast.success('Presente aceito e adicionado ao deck.');
      setDismissedGiftIds((prev) => [...prev, giftId]);
      setGiftOpen(false);
    } catch (err) {
      if (err instanceof AxiosError && err.response?.status === 409) {
        toast.error('Esse pokémon já está no deck escolhido.');
      } else {
        toast.error('Não foi possível aceitar o presente.');
      }
    }
  };

  const handleRejectGift = async (giftId: string) => {
    try {
      await rejectGift.mutateAsync(giftId);
      toast.info('Presente recusado e devolvido ao remetente.');
      setDismissedGiftIds((prev) => [...prev, giftId]);
      setGiftOpen(false);
    } catch {
      toast.error('Não foi possível recusar o presente.');
    }
  };

  if (!user) return null;

  return (
    <>
      <AppSidebar
        user={user}
        decks={decks}
        selectedDeckId={selectedDeckId ?? ''}
        onSelectDeck={handleSelectDeck}
        onCreateDeck={() => setCreateOpen(true)}
      />

      <main className="flex-1 overflow-y-auto">
        <header className="flex items-center justify-between border-b bg-white/60 px-6 py-5 backdrop-blur">
          <div>
            <p className="text-xs uppercase tracking-wider text-muted-foreground">Deck selecionado</p>
            <h1 className="text-2xl font-semibold text-primary">
              {selectedDeck?.name ?? (isLoadingDecks ? 'Carregando...' : 'Nenhum deck')}
            </h1>
          </div>
          <div className="flex items-center gap-2">
            {selectedDeck && (
              <Button
                variant="outline"
                size="sm"
                className="gap-2 border-destructive text-destructive hover:bg-destructive/10 hover:text-destructive"
                onClick={handleDelete}
                disabled={deleteDeck.isPending}
              >
                <Trash2 className="h-4 w-4" /> Remover deck
              </Button>
            )}
          </div>
        </header>

        <section className="p-6">
          {decksError && (
            <p className="mb-4 rounded-md bg-destructive/10 px-3 py-2 text-sm text-destructive">
              Não foi possível carregar seus decks.
            </p>
          )}

          {decks.length === 0 && !isLoadingDecks ? (
            <div className="flex flex-col items-center justify-center gap-3 rounded-lg border border-dashed py-20 text-center">
              <Sparkles className="h-10 w-10 text-secondary" />
              <h2 className="text-lg font-semibold">Você ainda não tem decks</h2>
              <p className="text-sm text-muted-foreground">
                Crie seu primeiro deck no botão de + na sidebar.
              </p>
              <Button className="mt-2" onClick={() => setCreateOpen(true)}>
                Criar deck
              </Button>
            </div>
          ) : isEmpty && selectedDeck ? (
            <div className="flex flex-col items-center justify-center gap-3 rounded-lg border border-dashed py-20 text-center">
              <Sparkles className="h-10 w-10 text-secondary" />
              <h2 className="text-lg font-semibold">Seu deck está vazio</h2>
              <p className="text-sm text-muted-foreground">
                Adicione Pokémons pelo Catálogo.
              </p>
            </div>
          ) : (
            <>
              <div className="grid gap-6 sm:grid-cols-2 xl:grid-cols-4">
                {pokemonsQuery.isLoading
                  ? Array.from({ length: PAGE_SIZE }).map((_, i) => <PokemonCardSkeleton key={i} />)
                  : items.map((pokemon) => (
                      <PokemonCard key={pokemon.id} pokemon={pokemon} deckId={selectedDeckId ?? undefined} />
                    ))}
              </div>
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
            </>
          )}
        </section>
      </main>

      <CreateDeckDialog
        open={createOpen}
        onOpenChange={setCreateOpen}
        onCreated={(deckId) => {
          setSelectedDeckId(deckId);
          setPage(0);
        }}
      />

      <GiftModal
        gift={currentGift}
        decks={decks}
        open={giftOpen && !!currentGift}
        onOpenChange={(open) => {
          setGiftOpen(open);
          if (!open && currentGift) {
            setDismissedGiftIds((prev) => [...prev, currentGift.id]);
          }
        }}
        onAccept={handleAcceptGift}
        onReject={handleRejectGift}
      />
    </>
  );
}
