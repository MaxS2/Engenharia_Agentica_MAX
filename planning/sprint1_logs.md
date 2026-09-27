# Log da Sprint 1: Setup Inicial

Registro cronológico das ações executadas pelo agente Programador durante a Sprint 1.

## 2026-04-17 — Início da Sprint 1

### Revisão de pré-requisitos
- Specs aprovadas pelo usuário.
- Correção aplicada em `archicterure-details.md`: tabela `gifts` passou a incluir `origin_deck_id`, `created_at`, `resolved_at` e `CHECK` no `status`. Endpoint `POST /gifts` passou a exigir `origin_deck_id` no payload. Endpoint `PATCH /gifts/{id}` documenta o fluxo de devolução em caso de recusa.
- Confirmado: não existe endpoint de remoção isolada de pokémon do deck (só via fluxo de Gift).
- Imagens `previa{n}.png` dispensadas — apenas descritivos textuais em `layout-details.md` §4.

### Ações planejadas desta sprint (sprint1_tasks.md)
1. Arquivos raiz: `.gitignore`, `.env.example`, `README.md`, `docker-compose.yml`, `cloudbuild.yaml`.
2. Backend scaffold em `./codebase/backend` (Spring Boot 3.2 / Java 21 / Maven) + `Dockerfile`.
3. Frontend scaffold em `./codebase/frontend` (Next.js 14 / TS / Tailwind / Shadcn) + `Dockerfile`.
4. Validação humana: `docker-compose up` sobe ambos; frontend em :3000, backend Swagger em :8080.

### Execução

#### Raiz do repositório
- `.gitignore` — cobre `.env`, `node_modules`, `.next`, `target/`, `*.db`, logs, IDEs e SO.
- `.env.example` — documenta todas as variáveis consumidas pelo compose (JWT, CORS, datasource, admin seeding, URL do backend para o frontend).
- `docker-compose.yml` — orquestra `backend` (porta 8080, volume `backend-data`) e `frontend` (porta 3000) com `depends_on`, `restart: unless-stopped` e build args.
- `cloudbuild.yaml` — pipeline de build/push/deploy no Cloud Run (projeto `pptnc-stage`, região `us-east1`), com volume Cloud Storage FUSE em `/data` e `--max-instances=1` conforme `infra-devops-details.md`.
- `README.md` — instruções de uso (`docker compose up --build`) e referências ao Swagger e ao usuário master.

#### Backend (`codebase/backend/`)
- `pom.xml` — Spring Boot 3.2.5 / Java 21 com Spring Web, Spring Security, Spring Data JPA, Validation, `sqlite-jdbc` 3.45, `hibernate-community-dialects` 6.4, SpringDoc OpenAPI 2.3, JJWT 0.12.
- `src/main/java/com/pptnc/pokedeck/PokedeckApplication.java` — entrypoint padrão Spring Boot.
- `src/main/java/com/pptnc/pokedeck/config/SecurityConfig.java` — `SecurityFilterChain` temporário que libera todos os endpoints (Sprint 4 substitui pelo filtro JWT).
- `src/main/java/com/pptnc/pokedeck/config/OpenApiConfig.java` — personaliza título, descrição e versão do Swagger.
- `src/main/resources/application.properties` — datasource SQLite parametrizado por env, JPA com dialeto SQLite da comunidade, paths do Swagger, placeholders para JWT/CORS/Admin seeding.
- `src/test/java/com/pptnc/pokedeck/PokedeckApplicationTests.java` — smoke test `contextLoads()`.
- `Dockerfile` multi-stage: `maven:3.9-eclipse-temurin-21-alpine` (build) → `eclipse-temurin:21-jre-alpine` (runtime), usuário não-root, volume `/data`, porta 8080.
- `.dockerignore`, `README.md` do asset.

#### Frontend (`codebase/frontend/`)
- `package.json` — Next.js 14.2.3, React 18, TypeScript 5, Tailwind 3.4, TanStack Query 5, Zod, React Hook Form, Axios, Lucide, e deps shadcn (class-variance-authority, clsx, tailwind-merge, tailwindcss-animate).
- `next.config.mjs` com `output: 'standalone'` (build otimizado para container).
- `tsconfig.json`, `tailwind.config.ts`, `postcss.config.mjs`, `.eslintrc.json`, `components.json` (pré-configuração do shadcn/ui com alias `@/` e baseColor slate).
- `src/app/layout.tsx`, `src/app/page.tsx`, `src/app/globals.css` (CSS vars da paleta `layout-details.md` §1.1 — primária azul `#2563EB` e secundária roxa `#7C3AED`).
- `src/lib/utils.ts` — helper `cn()` do shadcn.
- Pastas `components/`, `services/`, `hooks/`, `types/` criadas com `.gitkeep`.
- `Dockerfile` multi-stage: `node:20-alpine` (deps + build) → `node:20-alpine` (runtime standalone), usuário não-root `nextjs:1001`, porta 3000.
- `.dockerignore`, `.gitignore`, `README.md` do asset.

### Checkpoints para validação humana

O usuário deve executar e conferir:

1. Copiar o arquivo de ambiente:
   ```bash
   cp .env.example .env
   ```
2. Subir os dois containers:
   ```bash
   docker compose up --build
   ```
3. Validar:
   - [ ] `http://localhost:3000` exibe a home do scaffold Next.js (título "PPTNC Poke Deck").
   - [ ] `http://localhost:8080/swagger-ui/index.html` exibe o Swagger UI (ainda sem endpoints — a Sprint 2 adiciona os controllers).
   - [ ] `http://localhost:8080/v3/api-docs` retorna um JSON OpenAPI válido.
   - [ ] Estrutura de pastas `codebase/backend` e `codebase/frontend` está conforme `archicterure-details.md` e `sprint1_tasks.md`.

### Pós-validação
Após aprovação, realizar (pelo usuário ou pelo agente, mediante autorização):
- `git init` (repo ainda não inicializado) + primeiro commit.
- Adicionar remote `https://github.com/wellingtoncruz/pptnc-pokedeck`.
- Tag `v0.1.0-sprint1` e push.

### Observações e débitos técnicos da sprint
- `spring-boot-starter-actuator` não foi incluído (fora do escopo estrito). Consequência: healthcheck do docker-compose removido; se desejado no futuro, adicionar o starter e um endpoint `/actuator/health` liberado na `SecurityConfig`.
- `SecurityConfig` atual é **temporária e permissiva** — não deve ir para produção assim. A Sprint 4 fecha esse ponto.
- Volume `backend-data` do docker-compose é um volume nomeado do Docker. Em produção (Cloud Run) o `/data` será montado via Cloud Storage FUSE, conforme `cloudbuild.yaml` e `infra-devops-details.md`.
