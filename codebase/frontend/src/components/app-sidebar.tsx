'use client';

import Image from 'next/image';
import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import { Library, LogOut, Menu, Plus, Shield } from 'lucide-react';
import { useState } from 'react';

import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { Sheet, SheetContent, SheetTitle } from '@/components/ui/sheet';
import { useAuth } from '@/contexts/auth-context';
import { cn } from '@/lib/utils';
import type { Deck, User } from '@/types/domain';

interface AppSidebarProps {
  user: User;
  decks: Deck[];
  selectedDeckId: string;
  onSelectDeck: (deckId: string) => void;
  onCreateDeck?: () => void;
}

export function AppSidebar(props: AppSidebarProps) {
  const [mobileOpen, setMobileOpen] = useState(false);

  return (
    <>
      <aside className="hidden w-72 shrink-0 flex-col border-r bg-white md:flex">
        <SidebarBody {...props} onNavigate={() => undefined} />
      </aside>

      <button
        type="button"
        onClick={() => setMobileOpen(true)}
        className="fixed left-3 top-3 z-40 inline-flex h-10 w-10 items-center justify-center rounded-md border bg-white/90 text-muted-foreground shadow-sm backdrop-blur md:hidden"
        aria-label="Abrir menu"
      >
        <Menu className="h-5 w-5" />
      </button>

      <Sheet open={mobileOpen} onOpenChange={setMobileOpen}>
        <SheetContent side="left" className="p-0">
          <SheetTitle>Menu</SheetTitle>
          <SidebarBody {...props} onNavigate={() => setMobileOpen(false)} />
        </SheetContent>
      </Sheet>
    </>
  );
}

interface SidebarBodyProps extends AppSidebarProps {
  onNavigate: () => void;
}

function SidebarBody({
  user,
  decks,
  selectedDeckId,
  onSelectDeck,
  onCreateDeck,
  onNavigate,
}: SidebarBodyProps) {
  const pathname = usePathname();
  const router = useRouter();
  const { logout } = useAuth();

  const handleDeckClick = (deckId: string) => {
    if (pathname === '/') {
      onSelectDeck(deckId);
    } else {
      router.push(`/?deck=${deckId}`);
    }
    onNavigate();
  };

  return (
    <div className="flex h-full flex-col">
      <div className="flex items-center gap-3 border-b px-5 py-4">
        <Avatar className="h-10 w-10 ring-2 ring-secondary">
          <AvatarImage src="/iara.png" alt="IAra, mascote do PPT Nao Compila" />
          <AvatarFallback>IA</AvatarFallback>
        </Avatar>
        <div>
          <p className="text-sm font-semibold text-primary">PPTNC Poke Deck</p>
          <p className="text-xs text-muted-foreground">Olá, {user.username}</p>
        </div>
      </div>

      <nav className="flex-1 overflow-y-auto px-3 py-4">
        <div className="mb-3 flex items-center justify-between px-2">
          <span className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Meus Decks</span>
          <Button
            size="icon"
            variant="ghost"
            onClick={() => {
              onCreateDeck?.();
              onNavigate();
            }}
            aria-label="Novo deck"
          >
            <Plus className="h-4 w-4" />
          </Button>
        </div>
        <ul className="space-y-1">
          {decks.map((deck) => {
            const active = deck.id === selectedDeckId;
            return (
              <li key={deck.id}>
                <button
                  type="button"
                  onClick={() => handleDeckClick(deck.id)}
                  className={cn(
                    'flex w-full items-center justify-between rounded-md px-3 py-2 text-sm transition-colors',
                    active ? 'bg-primary text-primary-foreground' : 'hover:bg-accent',
                  )}
                >
                  <span className="truncate">{deck.name}</span>
                  <span
                    className={cn(
                      'rounded-full px-2 py-0.5 text-xs',
                      active ? 'bg-white/20' : 'bg-muted text-muted-foreground',
                    )}
                  >
                    {deck.pokemonCount}
                  </span>
                </button>
              </li>
            );
          })}
        </ul>
      </nav>

      <div className="border-t px-3 py-4 space-y-2">
        <Link
          href="/catalogo"
          onClick={onNavigate}
          className={cn(
            'flex items-center gap-2 rounded-md px-3 py-2 text-sm transition-colors',
            pathname === '/catalogo' ? 'bg-secondary text-secondary-foreground' : 'hover:bg-accent',
          )}
        >
          <Library className="h-4 w-4" />
          Catálogo
        </Link>
        {user.admin && (
          <Link
            href="/admin"
            onClick={onNavigate}
            className={cn(
              'flex items-center gap-2 rounded-md px-3 py-2 text-sm transition-colors',
              pathname === '/admin' ? 'bg-secondary text-secondary-foreground' : 'hover:bg-accent',
            )}
          >
            <Shield className="h-4 w-4" />
            Gerenciar Usuários
          </Link>
        )}
        <button
          type="button"
          onClick={() => {
            onNavigate();
            void logout();
          }}
          className="flex w-full items-center gap-2 rounded-md px-3 py-2 text-sm text-muted-foreground transition-colors hover:bg-accent"
        >
          <LogOut className="h-4 w-4" />
          Sair
        </button>
        <div className="flex items-center gap-2 pt-2">
          <Image src="/logo.png" alt="PPT Nao Compila" width={28} height={28} className="rounded" />
          <span className="text-[10px] text-muted-foreground">© 2026 PPT Não Compila</span>
        </div>
      </div>
    </div>
  );
}
