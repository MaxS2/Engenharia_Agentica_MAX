'use client';

import Image from 'next/image';
import { Check, Loader2, Plus } from 'lucide-react';

import { Button } from '@/components/ui/button';
import { Card, CardContent, CardFooter } from '@/components/ui/card';
import { capitalize, formatPokemonId } from '@/lib/format';
import { getTypeStyle } from '@/lib/pokemon-types';
import { cn } from '@/lib/utils';
import type { PokemonSummary } from '@/types/domain';

interface Props {
  pokemon: PokemonSummary;
  onAdd: () => void;
  adding?: boolean;
  disabled?: boolean;
}

export function CatalogPokemonCard({ pokemon, onAdd, adding, disabled }: Props) {
  const already = pokemon.inSelectedDeck;

  return (
    <Card className="flex flex-col overflow-hidden">
      <div className="relative aspect-square bg-gradient-to-br from-primary/10 to-secondary/10">
        <Image
          src={pokemon.imageUrl}
          alt={pokemon.name}
          fill
          sizes="(max-width: 768px) 100vw, 320px"
          className="object-contain p-4"
          unoptimized
        />
        <span className="absolute left-3 top-3 rounded-full bg-white/80 px-2 py-0.5 text-xs font-semibold text-muted-foreground backdrop-blur">
          {formatPokemonId(pokemon.id)}
        </span>
        {already && (
          <span className="absolute right-3 top-3 rounded-full bg-secondary px-2 py-0.5 text-xs font-semibold text-secondary-foreground">
            Já no deck
          </span>
        )}
      </div>
      <CardContent className="flex-1 space-y-3 pt-4">
        <h3 className="text-lg font-semibold">{capitalize(pokemon.name)}</h3>
        <div className="flex flex-wrap gap-1.5">
          {pokemon.types.map((type) => {
            const style = getTypeStyle(type);
            return (
              <span
                key={type}
                className={cn(
                  'inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-semibold',
                  style.bg,
                  style.text,
                )}
              >
                {style.label}
              </span>
            );
          })}
        </div>
      </CardContent>
      <CardFooter className="pt-0">
        <Button
          size="sm"
          className="w-full gap-2"
          variant={already ? 'outline' : 'default'}
          disabled={already || adding || disabled}
          onClick={onAdd}
        >
          {adding ? (
            <>
              <Loader2 className="h-4 w-4 animate-spin" /> Adicionando
            </>
          ) : already ? (
            <>
              <Check className="h-4 w-4" /> Já no deck
            </>
          ) : (
            <>
              <Plus className="h-4 w-4" /> Adicionar
            </>
          )}
        </Button>
      </CardFooter>
    </Card>
  );
}
