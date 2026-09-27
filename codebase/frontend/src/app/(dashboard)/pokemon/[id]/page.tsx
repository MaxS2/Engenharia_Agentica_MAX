'use client';

import Image from 'next/image';
import Link from 'next/link';
import { useParams, useRouter, useSearchParams } from 'next/navigation';
import { Suspense, useState } from 'react';
import { ArrowLeft, Send } from 'lucide-react';

import { StatBar } from '@/components/stat-bar';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Label } from '@/components/ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { useDecks } from '@/hooks/use-decks';
import { useRecipients, useSendGift } from '@/hooks/use-gifts';
import { usePokemonDetail } from '@/hooks/use-pokemons';
import { capitalize, formatPokemonId } from '@/lib/format';
import { getTypeStyle } from '@/lib/pokemon-types';
import { cn } from '@/lib/utils';
import { AxiosError } from 'axios';
import { toast } from 'sonner';

export default function PokemonDetailPage() {
  return (
    <Suspense fallback={<LoadingView />}>
      <PokemonDetailContent />
    </Suspense>
  );
}

function PokemonDetailContent() {
  const params = useParams<{ id: string }>();
  const searchParams = useSearchParams();
  const router = useRouter();
  const deckParam = searchParams.get('deck') ?? undefined;
  const pokemonId = Number(params.id);

  const { data: pokemon, isLoading, error } = usePokemonDetail(pokemonId, deckParam);
  const { data: decks = [] } = useDecks();
  const { data: recipients = [] } = useRecipients();
  const sendGift = useSendGift();

  const [sendOpen, setSendOpen] = useState(false);
  const [friendId, setFriendId] = useState('');
  const [originDeckId, setOriginDeckId] = useState<string>(deckParam ?? '');
  const [sendError, setSendError] = useState<string | null>(null);

  const handleSend = async () => {
    if (!friendId || !originDeckId || !pokemon) return;
    setSendError(null);
    try {
      await sendGift.mutateAsync({
        receiverId: friendId,
        pokemonId: pokemon.id,
        originDeckId,
      });
      setSendOpen(false);
      setFriendId('');
      toast.success('Presente enviado!');
      router.push(`/?deck=${originDeckId}`);
    } catch (err) {
      if (err instanceof AxiosError) {
        const status = err.response?.status;
        if (status === 409) {
          setSendError('Esse pokémon não está mais no deck de origem.');
        } else if (status === 403) {
          setSendError('Você não tem permissão sobre o deck de origem.');
        } else {
          setSendError('Não foi possível enviar o presente.');
        }
      } else {
        setSendError('Não foi possível enviar o presente.');
      }
    }
  };

  if (isLoading) return <LoadingView />;
  if (error || !pokemon) {
    return (
      <div className="flex-1 overflow-y-auto">
        <div className="mx-auto max-w-5xl px-6 py-8">
          <Button variant="ghost" className="gap-2 text-muted-foreground" onClick={() => router.back()}>
            <ArrowLeft className="h-4 w-4" /> Voltar
          </Button>
          <div className="mt-6 rounded-lg border border-dashed py-16 text-center text-muted-foreground">
            Não foi possível carregar os detalhes do pokémon.
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="flex-1 overflow-y-auto">
      <div className="mx-auto max-w-5xl px-6 py-8">
        <Button variant="ghost" className="gap-2 text-muted-foreground" onClick={() => router.back()}>
          <ArrowLeft className="h-4 w-4" /> Voltar ao Deck
        </Button>

        <div className="mt-4 grid gap-8 md:grid-cols-2">
          <Card className="overflow-hidden">
            <div className="relative aspect-square bg-gradient-to-br from-primary/15 via-secondary/10 to-primary/5">
              <Image
                src={pokemon.imageUrl}
                alt={pokemon.name}
                fill
                sizes="(max-width: 768px) 100vw, 480px"
                className="object-contain p-6"
                unoptimized
                priority
              />
              <span className="absolute left-4 top-4 rounded-full bg-white/80 px-2.5 py-1 text-xs font-semibold text-muted-foreground backdrop-blur">
                {formatPokemonId(pokemon.id)}
              </span>
            </div>
          </Card>

          <div className="space-y-6">
            <div>
              <h1 className="text-3xl font-semibold text-primary">{capitalize(pokemon.name)}</h1>
              <div className="mt-2 flex flex-wrap gap-1.5">
                {pokemon.types.map((type) => {
                  const style = getTypeStyle(type);
                  return (
                    <span
                      key={type}
                      className={cn(
                        'inline-flex items-center rounded-full px-3 py-1 text-xs font-semibold',
                        style.bg,
                        style.text,
                      )}
                    >
                      {style.label}
                    </span>
                  );
                })}
              </div>
              {pokemon.inSelectedDeck && (
                <p className="mt-2 text-xs text-secondary">Este pokémon está no deck selecionado.</p>
              )}
            </div>

            <Card>
              <CardHeader>
                <CardTitle className="text-lg">Estatísticas base</CardTitle>
              </CardHeader>
              <CardContent className="space-y-3">
                {pokemon.stats.map((stat) => (
                  <StatBar key={stat.label} label={stat.label} value={stat.baseValue} />
                ))}
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="text-lg">Informações</CardTitle>
              </CardHeader>
              <CardContent className="grid grid-cols-2 gap-4 text-sm">
                <div>
                  <p className="text-muted-foreground">Altura</p>
                  <p className="font-medium">{(pokemon.height / 10).toFixed(1)} m</p>
                </div>
                <div>
                  <p className="text-muted-foreground">Peso</p>
                  <p className="font-medium">{(pokemon.weight / 10).toFixed(1)} kg</p>
                </div>
                <div>
                  <p className="text-muted-foreground">Experiência base</p>
                  <p className="font-medium">{pokemon.baseExperience}</p>
                </div>
                <div>
                  <p className="text-muted-foreground">Habilidades</p>
                  <p className="font-medium">{pokemon.abilities.join(', ')}</p>
                </div>
              </CardContent>
            </Card>

            <div className="flex flex-col gap-2 sm:flex-row">
              <Button className="gap-2" onClick={() => setSendOpen(true)}>
                <Send className="h-4 w-4" /> Enviar a um amigo
              </Button>
              <Button variant="outline" asChild>
                <Link href="/">Voltar ao Deck</Link>
              </Button>
            </div>
          </div>
        </div>
      </div>

      <Dialog
        open={sendOpen}
        onOpenChange={(open) => {
          setSendOpen(open);
          if (!open) setSendError(null);
        }}
      >
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Enviar {capitalize(pokemon.name)} como presente</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="friend">Destinatário</Label>
              <Select value={friendId} onValueChange={setFriendId}>
                <SelectTrigger id="friend">
                  <SelectValue placeholder="Escolha um amigo" />
                </SelectTrigger>
                <SelectContent>
                  {recipients.length === 0 ? (
                    <SelectItem value="__empty" disabled>
                      Nenhum destinatário disponível
                    </SelectItem>
                  ) : (
                    recipients.map((f) => (
                      <SelectItem key={f.id} value={f.id}>
                        {f.username}
                      </SelectItem>
                    ))
                  )}
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-2">
              <Label htmlFor="origin">Deck de origem</Label>
              <Select value={originDeckId} onValueChange={setOriginDeckId}>
                <SelectTrigger id="origin">
                  <SelectValue placeholder="Selecione um deck" />
                </SelectTrigger>
                <SelectContent>
                  {decks.map((d) => (
                    <SelectItem key={d.id} value={d.id}>
                      {d.name}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            {sendError && (
              <p className="rounded-md bg-destructive/10 px-3 py-2 text-sm text-destructive">{sendError}</p>
            )}
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setSendOpen(false)}>
              Cancelar
            </Button>
            <Button
              disabled={!friendId || !originDeckId || sendGift.isPending}
              onClick={handleSend}
            >
              {sendGift.isPending ? 'Enviando...' : 'Enviar'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}

function LoadingView() {
  return (
    <div className="flex-1 overflow-y-auto">
      <div className="mx-auto max-w-5xl px-6 py-8">
        <div className="mt-4 grid gap-8 md:grid-cols-2">
          <div className="aspect-square animate-pulse rounded-lg bg-muted" />
          <div className="space-y-4">
            <div className="h-9 w-48 animate-pulse rounded bg-muted" />
            <div className="h-4 w-24 animate-pulse rounded bg-muted" />
            <div className="h-56 animate-pulse rounded bg-muted" />
            <div className="h-32 animate-pulse rounded bg-muted" />
          </div>
        </div>
      </div>
    </div>
  );
}
