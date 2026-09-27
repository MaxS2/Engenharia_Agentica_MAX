# PPTNC Poke Deck — Frontend

Interface web da aplicação PPTNC Poke Deck.

- **Stack:** Next.js 14 (App Router) · React 18 · TypeScript 5 · Tailwind CSS 3.4 · Shadcn/UI · TanStack Query · Zod · React Hook Form · Axios
- **Porta padrão:** `3000`

## Rodar localmente (sem Docker)

```bash
npm install
npm run dev
```

Abra <http://localhost:3000>.

## Rodar via Docker

A partir da raiz do repositório:

```bash
docker compose up --build frontend
```

## Variáveis de ambiente

| Variável | Descrição | Padrão |
| --- | --- | --- |
| `NEXT_PUBLIC_API_URL` | URL base do backend | `http://localhost:8080/api/v1` |

## Estrutura de pastas

```
src/
├── app/          # Rotas e layouts (App Router)
├── components/   # Componentes reutilizaveis (shadcn + proprios)
├── hooks/        # Hooks customizados (useDecks, etc)
├── lib/          # Utilitarios (cn, formatters)
├── services/     # Clients HTTP (axios)
└── types/        # Tipagens compartilhadas
```

## Adicionar componentes Shadcn

```bash
npx shadcn@latest add button card input
```

## Status do escopo

Este scaffold cobre apenas a **Sprint 1**: projeto sobe e renderiza a home. Telas de Login, Dashboard, Detalhes, Catálogo, Gift Modal e Admin serão construídas da Sprint 3 em diante.
