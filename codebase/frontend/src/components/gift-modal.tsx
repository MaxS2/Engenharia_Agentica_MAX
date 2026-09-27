'use client';

import Image from 'next/image';
import { useState } from 'react';
import { Gift as GiftIcon } from 'lucide-react';

import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogDescription,
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
import { capitalize } from '@/lib/format';
import type { Deck, Gift } from '@/types/domain';

interface GiftModalProps {
  gift: Gift | null;
  decks: Deck[];
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onAccept?: (giftId: string, deckId: string) => void;
  onReject?: (giftId: string) => void;
}

export function GiftModal({ gift, decks, open, onOpenChange, onAccept, onReject }: GiftModalProps) {
  const [selectedDeck, setSelectedDeck] = useState<string>(decks[0]?.id ?? '');

  if (!gift) return null;

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <div className="mx-auto mb-2 flex h-12 w-12 items-center justify-center rounded-full bg-secondary/15 text-secondary">
            <GiftIcon className="h-6 w-6" />
          </div>
          <DialogTitle className="text-center">Você recebeu um presente!</DialogTitle>
          <DialogDescription className="text-center">
            <strong>{gift.sender.username}</strong> enviou um Pokémon para você.
          </DialogDescription>
        </DialogHeader>

        <div className="flex flex-col items-center gap-3 py-2">
          <div className="relative h-40 w-40 rounded-xl bg-gradient-to-br from-primary/10 to-secondary/10">
            <Image
              src={gift.pokemonImageUrl}
              alt={gift.pokemonName}
              fill
              sizes="160px"
              className="object-contain p-3"
              unoptimized
            />
          </div>
          <p className="text-xl font-semibold">{capitalize(gift.pokemonName)}</p>
        </div>

        <div className="space-y-2">
          <Label htmlFor="gift-deck-select">Escolha o deck de destino</Label>
          <Select value={selectedDeck} onValueChange={setSelectedDeck}>
            <SelectTrigger id="gift-deck-select">
              <SelectValue placeholder="Selecione um deck" />
            </SelectTrigger>
            <SelectContent>
              {decks.map((deck) => (
                <SelectItem key={deck.id} value={deck.id}>
                  {deck.name}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>

        <DialogFooter className="gap-2 sm:gap-2">
          <Button
            variant="outline"
            className="border-destructive text-destructive hover:bg-destructive/10 hover:text-destructive"
            onClick={() => onReject?.(gift.id)}
          >
            Recusar
          </Button>
          <Button variant="success" onClick={() => onAccept?.(gift.id, selectedDeck)} disabled={!selectedDeck}>
            Aceitar
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
