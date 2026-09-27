# Code Review Adversarial - PPTNC Poke Deck

Este documento registra as vulnerabilidades e inconsistências encontradas pelo Agente Planejador após a conclusão do desenvolvimento da Sprint 9 e o status de resolução na Sprint 10.

## 1. Segurança e Autenticação

### [CR-01] Armazenamento de JWT em LocalStorage — ✅ RESOLVIDO (Sprint 10)
- **Local original:** `codebase/frontend/src/lib/auth-storage.ts`
- **Gravidade:** ALTA
- **Descrição:** O token JWT era armazenado em `localStorage`, tornando-o vulnerável a XSS.
- **Resolução:**
  - Backend passou a emitir o JWT em cookie `Set-Cookie` com `HttpOnly`, `Secure` (configurável via env), `SameSite=Strict`, `Path=/`, `Max-Age=86400` (`AuthController#login`, `JwtCookieProperties`).
  - Frontend removeu toda a lógica de `localStorage`: `auth-storage.ts` virou só uma constante de evento; `AuthContext` inicializa chamando `GET /auth/me` (200 = logado, 401 = não logado); Axios configurado com `withCredentials: true`.
  - Logout limpa o cookie via `Set-Cookie: <name>=; Max-Age=0`.

### [CR-02] Bypass de Middleware no Next.js — ✅ RESOLVIDO (Sprint 10)
- **Local original:** `codebase/frontend/src/middleware.ts`
- **Gravidade:** ALTA
- **Descrição:** O middleware decidia o acesso a rotas com um cookie auxiliar não assinado, permitindo forjar acesso visual ao dashboard/admin.
- **Resolução:**
  - Dependência `jose` adicionada (compatível com edge runtime).
  - `middleware.ts` extrai o JWT do cookie `pokedeck-session` e executa `jwtVerify` com o mesmo `JWT_SECRET` do backend. Rejeita qualquer token inválido ou expirado.
  - `admin` flag agora vem do payload do JWT real (claim `admin`), não de cookie forjável.
  - `JWT_SECRET` e `AUTH_COOKIE_NAME` injetados no container frontend via `docker-compose.yml`.

## 2. Lógica de Negócio e Integridade

### [CR-03] Race Condition no Envio de Presentes — ℹ️ MITIGADO (Sprint 10)
- **Local:** `GiftService#send`
- **Gravidade:** MÉDIA
- **Descrição:** Remoção do pokémon + persistência do gift estão na mesma `@Transactional`; em caso de falha de commit, todo o bloco faz rollback (Spring rola de volta a remoção de `deck_pokemons` também).
- **Resolução parcial:** Revisado. O método é transacional (AOP proxy), then nenhuma das escritas é aplicada se a transação falhar. A janela de inconsistência original só existiria se `deckPokemonRepository.deleteById` estivesse em transação separada, o que não é o caso. Documentado como aceitável. Retry automático fica como próximo passo se o Cloud Run identificar erros esporádicos.

### [CR-04] Destruição de Pokémon em Recusa Órfã — ✅ RESOLVIDO (Sprint 10)
- **Local:** `GiftService#reject`
- **Gravidade:** ALTA
- **Descrição:** Recusa com `origin_deck_id` deletado fazia o pokémon simplesmente sumir.
- **Resolução:** Implementado **Vault de Devolução** em `GiftService#resolveReturnDeck`:
  1. Se o deck de origem ainda existe → devolve ao deck original.
  2. Se não existe e o remetente tem outros decks → devolve ao primeiro deck do remetente (`WARN` estruturado com `VAULT:` no logger).
  3. Se o remetente ficou sem decks → cria um deck "Meu primeiro Deck" automaticamente e devolve nele. Log `VAULT: user '...' sem decks; criado deck ...`.

## 3. Performance e Escalabilidade

### [CR-05] Problema N+1 na Listagem de Presentes — ✅ RESOLVIDO (Sprint 10)
- **Local:** `GiftService#listPending`
- **Gravidade:** CRÍTICA
- **Descrição:** Cada item disparava chamada síncrona à PokeAPI.
- **Resolução:**
  - `GiftEntity` ganhou colunas denormalizadas `pokemon_name` e `pokemon_image_url` (Hibernate `ddl-auto=update` adiciona colunas nullable sem perda de dados).
  - `GiftService#send` consulta a PokeAPI **uma vez** no ato do envio e persiste os metadados. Falhas de rede caem no fallback estático (`pokemon-{id}` + URL de artwork padrão).
  - `listPending` e `toDto` agora usam os campos persistidos — zero chamadas externas na listagem. Se algum registro antigo não tiver os campos (migração suave), o fallback resolve preguiçosamente.

## 4. Consistência com Especificação

### [CR-06] Menu Mobile não implementado — ✅ RESOLVIDO (Sprint 10)
- **Local:** `codebase/frontend/src/components/app-sidebar.tsx`
- **Gravidade:** BAIXA
- **Descrição:** Sidebar sumia em viewports <768px, deixando o app sem navegação visual em mobile.
- **Resolução:**
  - Novo componente `components/ui/sheet.tsx` (padrão shadcn sobre `@radix-ui/react-dialog`).
  - `AppSidebar` refatorado: `SidebarBody` extraído como conteúdo único usado tanto pelo `<aside>` desktop quanto pelo `<Sheet>` mobile.
  - Botão hambúrguer fixo no canto superior esquerdo com `md:hidden` abre o drawer. Clicar em qualquer item do menu (deck, catálogo, admin, sair) fecha o drawer via callback `onNavigate`.
