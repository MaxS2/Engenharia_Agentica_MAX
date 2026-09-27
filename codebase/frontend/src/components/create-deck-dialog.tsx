'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { z } from 'zod';

import { toast } from 'sonner';

import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useCreateDeck } from '@/hooks/use-decks';

const schema = z.object({
  name: z.string().min(1, 'Informe um nome').max(100, 'Máximo 100 caracteres'),
});

type FormValues = z.infer<typeof schema>;

interface Props {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onCreated?: (deckId: string) => void;
}

export function CreateDeckDialog({ open, onOpenChange, onCreated }: Props) {
  const { mutateAsync, isPending, error, reset: resetMutation } = useCreateDeck();
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { name: '' },
  });

  useEffect(() => {
    if (!open) {
      reset();
      resetMutation();
    }
  }, [open, reset, resetMutation]);

  const onSubmit = async (values: FormValues) => {
    const deck = await mutateAsync(values.name);
    toast.success(`Deck "${deck.name}" criado.`);
    onCreated?.(deck.id);
    onOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>Novo deck</DialogTitle>
        </DialogHeader>
        <form className="space-y-4" onSubmit={handleSubmit(onSubmit)} noValidate>
          <div className="space-y-2">
            <Label htmlFor="deck-name">Nome do deck</Label>
            <Input id="deck-name" placeholder="ex: Lendários de Kanto" {...register('name')} />
            {errors.name && (
              <p className="text-xs text-destructive">{errors.name.message}</p>
            )}
          </div>
          {error && (
            <p className="rounded-md bg-destructive/10 px-3 py-2 text-sm text-destructive">
              Não foi possível criar o deck. Tente novamente.
            </p>
          )}
          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancelar
            </Button>
            <Button type="submit" disabled={isPending}>
              {isPending ? 'Criando...' : 'Criar'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
