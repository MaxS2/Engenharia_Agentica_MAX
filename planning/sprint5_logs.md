# Log da Sprint 5: Dashboard

## 2026-04-22 — Execução

### Backend: Persistência de pokémons nos decks
- `domain/DeckPokemonEntity` + `domain/DeckPokemonId` (Embeddable) — tabela `deck_pokemons` com PK composta `(deck_id, pokemon_id)` e `added_at`, conforme `archicterure-details.md` §2.3.
- `repository/DeckPokemonRepository` — `findAllByIdDeckIdOrderByAddedAtAsc` (paginado), `countByIdDeckId`, `existsByIdDeckIdAndIdPokemonId`, `deleteByIdDeckId`.

### Backend: DeckService
- `service/DeckService` centraliza as regras de posse: toda operação que toca um deck passa por `loadOwned(userId, deckId)` que retorna 404 se não existe, 403 se o deck é de outro usuário (regra §5 de `business-rules-details.md`).
- `listForUser(userId)` devolve decks do usuário com `pokemonCount` real vindo do count na tabela `deck_pokemons`.
- `create(userId, name)` / `delete(userId, deckId)` — remoção apaga cascata os `deck_pokemons` antes do deck.
- `listPokemons(userId, deckId, page, size)` — paginado via `Pageable`, default 4 conforme §2.1. Resolve metadados via `PokemonMetadataResolver`.
- `addPokemon(userId, deckId, pokemonId)` — rejeita duplicata com 409 (§3.1). Já habilitado aqui para apoiar a validação humana desta sprint; o fluxo completo via catálogo entra na Sprint 6.

### Backend: PokemonMetadataResolver
- `service/PokemonMetadataResolver` — mapa estático com nome/tipos dos pokémons conhecidos (bulbasaur, charmander, squirtle, pikachu, eevee, mewtwo) + fallback `pokemon-{id}`. A URL de artwork segue o padrão PokeAPI official-artwork. Substituído pela PokeAPI real na Sprint 7.

### Backend: Controller real
- `DeckController` refatorado: todos os endpoints usam `@AuthenticationPrincipal AuthenticatedUser` e delegam ao `DeckService`. Anotação `@SecurityRequirement(name = "bearerAuth")` exposta no Swagger.
- Endpoints:
  - `GET /api/v1/decks` — decks do logado.
  - `POST /api/v1/decks` — cria deck.
  - `DELETE /api/v1/decks/{id}` — remove deck (com cascata dos pokémons).
  - `GET /api/v1/decks/{id}/pokemons?page=&size=` — listagem paginada.
  - `POST /api/v1/decks/{id}/pokemons` — adiciona pokémon (habilitado cedo para validação).

### Frontend: TanStack Query
- `providers/query-provider.tsx` — `QueryClientProvider` com `staleTime` 30s e `refetchOnWindowFocus` desativado.
- `app/layout.tsx` envolve `AuthProvider` com o `QueryProvider` (query no nível raiz para suportar hooks em qualquer página).
- `services/decks-service.ts` — `listDecks`, `createDeck`, `deleteDeck`, `listDeckPokemons`, `addPokemonToDeck`, tipando `PageResponse<T>`.

### Frontend: Hooks
- `hooks/use-decks.ts` — `useDecks`, `useCreateDeck`, `useDeleteDeck`, `useDeckPokemons`, `useAddPokemonToDeck`. Invalidação por chave (`['decks']`, `['decks', deckId, 'pokemons']`) para manter UI consistente após mutations. `useDeckPokemons` usa `keepPreviousData` para transição suave entre páginas.

### Frontend: Dashboard integrado
- `app/(dashboard)/page.tsx` consome `useDecks` + `useDeckPokemons`. Seleção de deck é automática: ao logar, o primeiro deck vira o ativo; ao remover o deck selecionado, cai para "nenhum deck" e mostra estado vazio com CTA para criar.
- Botão **"Remover deck"** no header (com `confirm()`) dispara `useDeleteDeck`; a invalidação atualiza sidebar e contadores.
- Botão **"Novo deck"** (ícone `+` na sidebar) abre `components/create-deck-dialog.tsx` — form RHF+Zod, trata erro de rede, seleciona automaticamente o deck criado.
- Paginação real: aparece apenas se `totalPages > 1`. Os chevrons navegam `page` e o `keepPreviousData` evita flash entre páginas.
- Empty states específicos: "Você ainda não tem decks" (zero decks) e "Seu deck está vazio" (deck selecionado sem pokémons, com nota sobre o catálogo da Sprint 6).
- `app/(dashboard)/admin/page.tsx` — sidebar agora consome `useDecks` em vez de `MOCK_DECKS`.

### Builds validados
- Backend: `mvn clean compile` — BUILD SUCCESS (41 classes, +5 vs Sprint 4).
- Frontend: `npm run build` — 6 rotas, middleware 27 kB. Dashboard cresceu para 5.8 kB.

### Checkpoints para validação humana

1. Suba backend + frontend (ver log da Sprint 4 para os comandos).
2. Logue com `admin/admin`. O dashboard deve carregar com "Meu primeiro Deck" (criado pelo seed/sprint 4), vazio.
3. Clique no `+` da sidebar e crie dois decks extras (ex.: "Iniciais", "Lendários"). O dashboard deve alternar automaticamente para o recém-criado.
4. Clique em cada deck na sidebar; o header mostra o nome, e o empty state aparece enquanto os decks estiverem vazios.
5. Adicione pokémons via `POST /api/v1/decks/{id}/pokemons` (use o botão "Authorize" do Swagger com o token) — IDs conhecidos como 25 (Pikachu), 1 (Bulbasaur), 4 (Charmander), 7 (Squirtle), 133 (Eevee), 150 (Mewtwo) mostram nomes/tipos; outros IDs aparecem como `pokemon-{id}` até a Sprint 7.
   - Alternativa SQL: `INSERT INTO deck_pokemons(deck_id, pokemon_id, added_at) VALUES ('<deck>', 25, '2026-04-22T00:00:00Z');`
6. Adicione 5+ pokémons no mesmo deck e verifique: a paginação aparece após 4, "Anterior/Próximo" navega sem flash.
7. "Remover deck" pede confirmação e limpa todos os pokémons associados.
8. Crie um usuário comum (`/admin`) e confirme que ele vê apenas o próprio "Meu primeiro Deck" — não os do admin.

### Observações e débitos técnicos
- **`POST /decks/{id}/pokemons`** foi ativado antes da hora (era escopo da Sprint 6) só para suportar a validação humana. Na Sprint 6 ele será chamado pela tela de catálogo de verdade. A validação de duplicata já está correta.
- **Nomes/tipos dos pokémons:** `PokemonMetadataResolver` tem apenas 6 entradas hard-coded + fallback. A Sprint 7 (proxy PokeAPI) substitui pelo fetch real com cache.
- **Página de detalhes (`/pokemon/[id]`)** continua alimentada por `mockPokemonDetail` — integração com backend real é Sprint 7.
- **Gift modal** continua usando `MOCK_PENDING_GIFT` — Sprint 8.
- **React Query SSR / hydration:** não usamos prefetching no servidor. Se quisermos melhorar TTFB, dá pra introduzir `HydrationBoundary` no dashboard no polimento da Sprint 9.
- **Ordenação dos pokémons:** fixa em `added_at ASC`. Se o usuário quiser reordenar/favoritar, vira feature nova (fora do escopo).
