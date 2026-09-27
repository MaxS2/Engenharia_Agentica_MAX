import type { Metadata } from 'next';
import './globals.css';

import { Toaster } from 'sonner';

import { AuthProvider } from '@/contexts/auth-context';
import { QueryProvider } from '@/providers/query-provider';

export const metadata: Metadata = {
  title: 'PPTNC Poke Deck',
  description: 'Monte e compartilhe seus decks de Pokémon. Projeto pedagógico do podcast PPT Não Compila.',
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="pt-BR">
      <body className="min-h-screen antialiased">
        <QueryProvider>
          <AuthProvider>{children}</AuthProvider>
        </QueryProvider>
        <Toaster richColors closeButton position="top-right" />
      </body>
    </html>
  );
}
