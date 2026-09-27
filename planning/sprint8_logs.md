# Log da Sprint 8: Mecanismo de Gift

## 2026-04-22 — Execução

### Backend: Gift persistence
- `domain/GiftEntity` — mapeia tabela `gifts` (sender_id, receiver_id, pokemon_id, origin_deck_id, status, created_at, resolved_at) conforme `archicterure-details.md` §2.4. `GiftStatus` é persistido como `VARCHAR` (JPA `EnumType.STRING`), batendo com o CHECK do spec.
- `repository/GiftRepository` — `findAllByReceiver_IdAndStatusOrderByCreatedAtAsc(receiverId, status)` para a caixa de entrada pendente.

### Backend: GiftService transacional
- `service/GiftService` centraliza os três fluxos (`@Transactional`):
  - **`send(senderId, request)`** — valida `sender != receiver`, carrega entities, confere que o `originDeck` pertence ao sender e que o pokémon está lá (409 caso contrário), remove o registro de `deck_pokemons` e cria `GiftEntity` com `status=PENDING`. Tudo na mesma transação → rollback automático se qualquer etapa falhar (spec §4.1 "operação transacional").
  - **`accept(receiverId, giftId, targetDeckId)`** — valida posse via `loadPendingOwnedByReceiver` (idempotência: 409 se já resolvido), confere que o `targetDeck` é do receiver, garante que o pokémon ainda não está nele, insere em `deck_pokemons` e marca `status=ACCEPTED` + `resolved_at=now`.
  - **`reject(receiverId, giftId)`** — mesma validação de posse. Se o `origin_deck_id` ainda existe (o sender não apagou o deck), devolve o pokémon (`INSERT` em `deck_pokemons`) apenas se ele não estiver lá; do contrário apenas loga e ignora. Marca `status=REJECTED` + `resolved_at=now`.
- Metadados do pokémon para o DTO vêm de `PokeApiService.detailById` com fallback estático em caso de falha.

### Backend: Controllers e segurança
- `GiftController` reescrito — usa `@AuthenticationPrincipal` para extrair o user logado. `PATCH /gifts/{id}` delega para `accept` ou `reject` conforme o status do payload; outros valores retornam 400.
- `UserController` ganhou `GET /api/v1/users/recipients` — retorna todos os usuários (menos o logado), ordenados por username. Disponível para qualquer autenticado (não só admin).
- `SecurityConfig` — nova regra `GET /api/v1/users/recipients → authenticated()` inserida **antes** de `/api/v1/users/** → hasRole(ADMIN)` (ordem importa).

### Frontend: Services + hooks
- `services/gifts-service.ts` — `listPendingGifts`, `sendGift`, `acceptGift`, `rejectGift`.
- `services/users-service.ts` ganhou `listRecipients`.
- `hooks/use-gifts.ts` — `usePendingGifts`, `useRecipients`, `useSendGift`, `useAcceptGift`, `useRejectGift`. Todas as mutations invalidam `['decks']` (pra atualizar contadores e conteúdo) e `['gifts', 'pending']` (pra fechar a caixa de entrada).

### Frontend: Dashboard intercepta pendentes
- `app/(dashboard)/page.tsx` — `usePendingGifts(!!user)` é disparado toda vez que o user loga ou volta ao dashboard. `currentGift` pega o primeiro não-descartado localmente e abre o `GiftModal` automaticamente (cumprindo `business-rules-details.md` §4.2: "Se houver presentes pendentes, o Gift Modal deve ser exibido imediatamente após o Dashboard carregar").
- `dismissedGiftIds` em state local permite navegar entre múltiplos presentes sem reabrir os já tratados na sessão.
- Aceitar/recusar chamam `useAcceptGift`/`useRejectGift`, com tratamento de erro 409 ("pokémon já está no deck escolhido").
- Botão "Simular presente" removido (era só de placeholder, agora o modal abre de verdade).

### Frontend: Envio real
- `app/(dashboard)/pokemon/[id]/page.tsx` — diálogo "Enviar a um amigo" agora consome `useRecipients()` (destinatários reais, exceto o logado) e `useSendGift()`.
- Default do `originDeckId` vem do `?deck=<id>` da URL; caso contrário, usuário escolhe entre `useDecks()`.
- Sucesso → `router.push(/?deck=<origin>)` pra levar o user ao dashboard ver que o pokémon saiu do deck.
- Erros tratados: 409 (pokémon não está mais no deck), 403 (deck não é seu), demais.

### Ajustes colaterais
- `MOCK_PENDING_GIFT` e `MOCK_USERS` (no diálogo de envio) não são mais usados no fluxo principal. `MOCK_USERS` ainda existe mas só é referenciado em testes/documentação pendente.

### Builds validados
- Backend: `mvn clean compile` — BUILD SUCCESS (46 classes, +3 vs Sprint 6/7).
- Frontend: `npm run build` — 7 rotas, `/pokemon/[id]` cresceu pra 7.11 kB.

### Checkpoints para validação humana (fluxo ponta-a-ponta)
1. Suba backend + frontend. Logue como `admin/admin`.
2. Crie usuário `ash` via `/admin` (senha `ash123`).
3. Admin: vá ao **Catálogo**, adicione o Pikachu (ID 25) ao "Meu primeiro Deck".
4. No dashboard do admin, clique em "Meu primeiro Deck" — Pikachu aparece.
5. Abra **Ver Detalhes** do Pikachu. Clique **Enviar a um amigo** → escolha `ash` como destinatário, "Meu primeiro Deck" como origem, **Enviar**.
6. Volte ao dashboard do admin — o Pikachu **sumiu** do deck (removido transacionalmente).
7. **Sair** (botão na sidebar). Logue como `ash/ash123`.
8. Assim que o dashboard carrega, o **Gift Modal** abre mostrando o Pikachu enviado pelo admin.
9. Clique **Recusar**. Modal fecha.
10. **Sair** e logue novamente como `admin/admin`. Vá em "Meu primeiro Deck" → **o Pikachu voltou** (regra §4.1 "devolvido automaticamente ao deck de origem").
11. Repita o envio; agora, ao logar como `ash`, clique **Aceitar** e escolha o "Meu primeiro Deck" dele. O modal fecha e o dashboard do ash mostra o Pikachu.
12. Re-logue como admin → Pikachu **não** voltou (foi aceito pelo destinatário).
13. Tente aceitar o mesmo gift duas vezes via Swagger → segunda chamada retorna 409 "presente ja resolvido".

### Observações e débitos técnicos
- **Admin deleta usuário com gifts pendentes:** hoje o `UserService.delete` dispara o cascade do `OneToMany decks` → `deleteByIdDeckId` nos `deck_pokemons`. Os `gifts` relacionados (sender ou receiver) NÃO têm FK cascade; o SQLite pode manter os registros órfãos. Documentado como débito — a Sprint 9 deve adicionar `ON DELETE CASCADE` ou limpar explicitamente no service.
- **Notificações fora da sessão:** o modal só abre quando o user abre/navega no dashboard. Sem WebSocket nem polling contínuo — se o user está em `/catalogo`, ele não vê o modal até voltar pra `/`. Aceitável pro escopo.
- **Idempotência em cliques rápidos:** o service lança 409 se o gift já foi resolvido. O frontend pode mostrar erro genérico pro user mas o estado fica consistente.
- **Múltiplos presentes pendentes:** tratados um por vez. O usuário recusa/aceita um, o dashboard re-renderiza e o próximo aparece.
- **Deck de origem apagado:** na recusa, o pokémon some em vez de voltar — behavior documentado como aceitável (evita recriação silenciosa de deck apagado).
- **`MOCK_USERS` / `MOCK_PENDING_GIFT`** não são mais consumidos em rotas reais — ficam apenas para referência histórica. Dá pra remover no polimento (Sprint 9).
