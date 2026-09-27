# Log da Sprint 10: Hardening e Prontidão para Produção

## 2026-04-22 — Planejamento Concluído
- Sprint 10 definida para endereçar os achados da Revisão Adversarial (Sprint 9).
- Foco em Segurança (Cookies), Performance (N+1) e UX Mobile.
- Estratégia de denormalização escolhida para a API de Gifts para garantir latência zero.

## 2026-04-22 — Execução

### Backend: Cookie HttpOnly (CR-01)
- `security/JwtCookieProperties` — nova fonte única para `pokedeck.auth.cookie.{name,secure,same-site,path}` (todos sobrescritíveis via env).
- `AuthController` reescrito para emitir `ResponseCookie` com `HttpOnly`, `Secure` (padrão false em dev, true em prod), `SameSite=Strict`, `Path=/`, `Max-Age` casado com `expirationMs` do JWT. Também passou a oferecer:
  - `GET /api/v1/auth/me` — retorna o `UserDTO` do principal autenticado. Usado pelo frontend para reconstruir o estado de sessão sem depender de `localStorage`.
  - `POST /api/v1/auth/logout` — agora responde com cookie vazio (`Max-Age=0`) para invalidar a sessão no cliente.
- `JwtAuthenticationFilter` lê o token primeiro do cookie configurado; mantém fallback para `Authorization: Bearer` (útil pro Swagger).
- `SecurityConfig` libera `/api/v1/auth/me` apenas para autenticados.
- `application.properties` ganhou o bloco `pokedeck.auth.cookie.*`.

### Backend: Denormalização de gifts (CR-05)
- `GiftEntity` — novas colunas `pokemon_name` (VARCHAR 100) e `pokemon_image_url` (VARCHAR 500). Hibernate `ddl-auto=update` adiciona em instalações existentes sem migração destrutiva.
- `GiftService#send` — consulta a PokeAPI uma vez via `resolveMetadata`, já com fallback estático, e persiste no `GiftEntity`. Envio antigo continua funcionando porque `resolveMetadata` sempre produz algo.
- `GiftService#toDto` e `listPending` usam `gift.getPokemonName()` / `getPokemonImageUrl()`. Zero chamadas externas na listagem. Se encontrar gifts legados sem esses campos, lazy-resolve uma vez via PokeAPI (fallback de compatibilidade).

### Backend: Vault de Devolução (CR-04)
- Novo método `resolveReturnDeck(gift)` no `GiftService`:
  1. Retorna o deck de origem se existir.
  2. Se não, pega o primeiro deck do sender (`deckRepository.findAllByOwner_Id`).
  3. Se o sender não tem deck nenhum, cria um `"Meu primeiro Deck"` automaticamente (persistido na mesma transação).
- Em todos os casos fora do happy path, loga em `WARN` com o prefixo `VAULT:` + gift id, pokemon id, username e deck escolhido — trilha de auditoria que pode ser filtrada em logs.
- `reject` agora sempre devolve o pokémon (não há mais o cenário "loga e ignora").

### Frontend: Remoção de localStorage + withCredentials (CR-01)
- `lib/auth-storage.ts` limpo — exporta apenas `AUTH_EVENT_EXPIRED`. Nenhuma leitura/escrita de `localStorage` restante.
- `services/api.ts` — `withCredentials: true` no axios; interceptor de request simplificado (sem token no header); 401 continua disparando o evento `pokedeck-auth:expired`.
- `services/auth-service.ts` ganhou `fetchMe()` apontando para `GET /auth/me`.
- `contexts/auth-context.tsx` — na montagem, chama `fetchMe()`; sucesso popula `user`, falha limpa. Login/logout invalidam `QueryClient` (já introduzido em sprint anterior). A estrutura de cache-busting entre usuários foi mantida.

### Frontend: Middleware validando JWT (CR-02)
- `npm install jose` para ter HMAC verify compatível com edge runtime.
- `middleware.ts` agora:
  - Lê o cookie `AUTH_COOKIE_NAME` (default `pokedeck-session`).
  - Decodifica e verifica com `jwtVerify(token, secret)` usando `process.env.JWT_SECRET`.
  - Se a verificação falhar (falta de secret, assinatura inválida, expirado), trata como não-autenticado.
  - Acesso a `/admin` exige claim `admin === true`.
- `docker-compose.yml` — `JWT_SECRET` e `AUTH_COOKIE_NAME` repassados ao container frontend para casar com o backend.

### Frontend: UX mobile (CR-06)
- `components/ui/sheet.tsx` novo — wrapper shadcn sobre `@radix-ui/react-dialog` (Root, Content, Overlay, Trigger, Close, Title). Portal + botão X de fechar.
- `components/app-sidebar.tsx` refatorado:
  - `SidebarBody` extraído como corpo único (user, decks, Catálogo, Admin, Sair, logo).
  - Desktop: `<aside hidden md:flex>` envolve o `SidebarBody`.
  - Mobile: botão hambúrguer fixo (`fixed top-3 left-3`, só `md:hidden`) abre um `<Sheet>` com o mesmo `SidebarBody`; após qualquer navegação interna (deck, link, logout), o drawer fecha via callback `onNavigate`.
- Nenhum consumidor (`/`, `/catalogo`, `/admin`) precisou ser alterado: o `AppSidebar` já renderiza ambas as variantes internamente.

### Builds validados
- Backend: `mvn clean compile` — BUILD SUCCESS (47 classes, +1 vs Sprint 9).
- Frontend: `npm run build` — 7 rotas. Middleware cresceu pra 32.8 kB (por conta do `jose`); demais rotas mudaram pouco.

### Checkpoints para validação humana
1. `docker compose up --build`, abrir `http://localhost:3000` e logar com `admin/admin`.
2. **DevTools > Application > Cookies**: confirme que existe `pokedeck-session` (HttpOnly ✅, SameSite Strict) e que `localStorage` está vazio no domínio.
3. Tentar forjar `pokedeck-session=abcdef` (inválido) e acessar `/admin` → middleware redireciona pra `/login`.
4. Em `/admin`, criar um usuário `ash`, dar logout, logar com `ash` → se tentar `/admin`, redireciona pra `/` (claim `admin=false` no JWT).
5. **N+1**: admin envia 3 gifts de pokémons diferentes pro ash; ao logar como ash, abrir DevTools > Network, filtrar `pokeapi.co` — nenhuma chamada externa dispara na renderização do modal. Os metadados vêm do DB.
6. **Vault**: admin envia gift pro ash → admin deleta o deck de origem → ash recusa → re-logar como admin e ver o pokémon em "Meu primeiro Deck" (ou deck automaticamente criado se o admin tiver apagado todos).
7. **Mobile**: abrir DevTools > Toggle device toolbar (iPhone SE, Pixel 7). O sidebar aparece como botão de hambúrguer fixo no canto superior esquerdo. Clicar abre drawer; clicar em qualquer item fecha o drawer e navega.

### Observações e débitos técnicos restantes
- **`Secure` flag:** fica `false` em dev (HTTP localhost). Em produção precisa `AUTH_COOKIE_SECURE=true` e HTTPS — documentado no `README.md` como configuração de deploy.
- **CORS cross-origin:** o cookie viaja porque o backend está em `localhost:8080` e o frontend em `localhost:3000` (same-site, portas diferentes). Para domínios distintos em produção, trocar `SameSite=Strict` por `SameSite=Lax` e ajustar `allowCredentials`.
- **`Flyway/Liquibase`:** ainda na lista de débitos (tech-debits.md). Colunas novas foram adicionadas via `ddl-auto=update` — para produção real, precisa migração versionada.
- **`Rate limit`:** em aberto (tech-debits.md).
- **Gifts legados sem metadados:** o fallback preguiçoso cuida, mas um comando de `data migration` pode reprocessar tudo no primeiro boot pós-deploy.
