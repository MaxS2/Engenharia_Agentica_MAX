# Log da Sprint 4: Autenticação e Gestão

## 2026-04-22 — Execução

### Backend: Persistência de usuários e decks
- `domain/UserEntity` e `domain/DeckEntity` (JPA) mapeando `users` e `decks` conforme `archicterure-details.md` §2.1 e §2.2. IDs armazenados como string (UUID) para compatibilidade com SQLite.
- `repository/UserRepository` (`findByUsername`, `existsByUsername`) e `repository/DeckRepository`.
- Tabelas `deck_pokemons` e `gifts` ficam para as Sprints 6 e 8 — escopo da Sprint 4 cobre apenas o necessário para o fluxo de autenticação + primeiro deck por usuário.

### Backend: Segurança JWT
- `security/Sha256PasswordEncoder` — hash SHA-256 sem salt, conforme `business-rules-details.md` §5.
- `security/JwtTokenProvider` — emissão e validação do token usando `jjwt` 0.12.5, com claims `sub` (UUID do usuário), `username` e `admin`. Segredo e TTL lidos de `pokedeck.jwt.secret` e `pokedeck.jwt.expiration-ms`.
- `security/JwtAuthenticationFilter` (`OncePerRequestFilter`) — extrai `Authorization: Bearer ...`, popula o `SecurityContext` e limpa em caso de token inválido.
- `security/AuthenticatedUser` — principal com `ROLE_ADMIN` / `ROLE_USER`.
- `security/CustomUserDetailsService` — carrega `UserEntity` pelo username para o `AuthenticationManager`.
- `config/SecurityConfig` reescrito: stateless, CSRF off, CORS já global via `CorsConfig`, allowlist para `/api/v1/auth/login`, `/v3/api-docs/**` e `/swagger-ui/**`; `/api/v1/users/**` exige `ROLE_ADMIN`; demais endpoints autenticados. Respostas 401/403 explícitas via `authenticationEntryPoint` e `accessDeniedHandler`.
- `config/OpenApiConfig` ganhou security scheme `bearerAuth` (JWT), e `UserController` anotado com `@SecurityRequirement(name = "bearerAuth")` para o Swagger.

### Backend: Serviços e seed
- `service/UserService` — `list`, `create` (com criação automática do deck "Meu primeiro Deck" em cascata, cumprindo §1.2), `delete` (bloqueia auto-remoção conforme §1.1; 404 se não existir; 409 em conflito de username).
- `service/AdminSeeder` — `CommandLineRunner` que cria o admin inicial (default `admin`/`admin`) + deck padrão no primeiro boot. Idempotente via `existsByUsername`.

### Backend: Controllers reais
- `AuthController` agora usa `AuthenticationManager` real e emite JWT via `JwtTokenProvider`. Retorna 401 em credenciais inválidas. Logout permanece no-op (JWT stateless).
- `UserController` encapsula o `UserService`, lê o admin logado via `@AuthenticationPrincipal` para `delete`.

### Frontend: Camada de autenticação
- `services/api.ts` — cliente Axios com `NEXT_PUBLIC_API_BASE_URL` (default `http://localhost:8080/api/v1`), interceptor de request anexando Bearer e interceptor de response que limpa o estado e dispara `pokedeck-auth:expired` em 401.
- `lib/auth-storage.ts` — persistência em `localStorage` (token + user + expiresAt) e espelho em cookie `pokedeck-auth` (apenas `admin` + `username`, sem token) para o middleware Next.js.
- `services/auth-service.ts` — `login`, `logout`.
- `services/users-service.ts` — `listUsers`, `createUser`, `deleteUser`.
- `contexts/auth-context.tsx` — `AuthProvider` + `useAuth` gerenciando `user`, `isAuthenticated`, login/logout, redireciona para `/login` ao expirar. Provider montado no `app/layout.tsx`.

### Frontend: Proteção de rotas
- `middleware.ts` — redireciona para `/login` rotas privadas sem cookie, redireciona para `/` se já logado na tela de login, e bloqueia `/admin` para não-admins. Matcher exclui estáticos, `favicon.ico`, `logo.png`, `iara.png`.

### Frontend: Telas integradas
- `/login` — formulário com React Hook Form + Zod, tratamento de 401 ("Usuário ou senha inválidos"), redireciona para `?next=` ou `/`. Envelopado em `<Suspense>` para satisfazer o Next 14 quanto ao `useSearchParams`.
- `/admin` — lista usuários via `GET /users`, criação com validação Zod, remoção com `confirm` e proteção contra deletar o próprio admin logado.
- `components/app-sidebar.tsx` — botão "Sair" agora usa `useAuth().logout()` em vez do link estático.
- `/` (Dashboard) — passa o `user` real do `AuthContext` para a sidebar; decks/pokemons seguem mockados (entra na Sprint 5).

### Configuração
- `application.properties` já definia `pokedeck.jwt.*` e `pokedeck.admin.*` desde a Sprint 1; apenas passaram a ser consumidos.
- Nenhuma nova dependência no `pom.xml` (jjwt, security, jpa, sqlite, validation já estavam).
- Nenhuma nova dependência no `package.json` (axios, zod, react-hook-form, @hookform/resolvers já estavam).

### Builds validados
- Backend: `mvn clean compile` — BUILD SUCCESS (36 classes via container `maven:3.9-eclipse-temurin-21`).
- Frontend: `npm run build` — compila, passa type-check, 6 rotas prerenderizadas.

### Checkpoints para validação humana

Subir tudo com Docker Compose:

```bash
docker compose up --build
```

Ou local (backend em um terminal, frontend em outro):

```bash
# Backend — precisa JDK 21 + Maven, ou use Docker:
docker run --rm -p 8080:8080 \
  -v $(pwd)/codebase/backend:/src \
  -v $HOME/.m2:/root/.m2 \
  -w /src maven:3.9-eclipse-temurin-21 \
  mvn spring-boot:run
```

```bash
# Frontend
cd codebase/frontend
npm run dev
```

Abra `http://localhost:3000` e confira:

- [ ] Acessar qualquer rota privada (`/`, `/admin`) sem estar logado redireciona para `/login?next=...`.
- [ ] Logar com `admin/admin` — redireciona para `/` e mantém usuário logado após refresh.
- [ ] Logar com credenciais erradas — exibe mensagem "Usuário ou senha inválidos".
- [ ] Em `/admin`: criar novo usuário (`ash/ash123`). Aparece na tabela imediatamente.
- [ ] Sair com "Sair" na sidebar → redireciona pra `/login`.
- [ ] Logar com `ash/ash123` — consegue entrar no dashboard.
- [ ] Como `ash`, tentar acessar `/admin` diretamente na URL → é redirecionado para `/`.
- [ ] `GET /api/v1/users` sem Authorization → 401. Com token não-admin → 403.
- [ ] Swagger em `/swagger-ui/index.html` com botão "Authorize" aceita o JWT.

### Observações e débitos técnicos
- **Entidades `deck_pokemons` e `gifts`:** ainda não existem — serão criadas na Sprint 6 (catálogo) e Sprint 8 (gifts). Por ora só `users` e `decks` são persistidas.
- **Mock data residual:** `MOCK_DECKS`, `MOCK_POKEMON_LIST`, `MOCK_PENDING_GIFT` continuam alimentando o dashboard e a página de detalhes. A integração desses endpoints é a Sprint 5.
- **Cookie do middleware:** guarda apenas `admin` + `username` (não o token). O token real mora só em `localStorage` para minimizar risco de CSRF; o middleware usa o cookie como dica de sessão. Se quisermos SSR autenticado no futuro, migramos pra httpOnly.
- **SHA-256 sem salt:** requisito explícito do `archicterure.md`/`business-rules-details.md` §5. Documentado como risco no `archicterure-details.md` §6.
- **Rate limit / refresh token:** fora do escopo; JWT tem TTL de 24h e o cliente simplesmente reautentica.
- **Logout server-side:** continua no-op. Se passarmos para denylist de JWT, entra como tech debt na Sprint 9.
