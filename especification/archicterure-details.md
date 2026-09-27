# Detalhamento de Arquitetura - PPTNC Poke Deck

Este documento detalha as especificações técnicas para o desenvolvimento do projeto, expandindo as definições de `archicterure.md`.

## 1. Stack Tecnológica e Versões Mínimas

### 1.1 Frontend
- **Framework:** Next.js 14.2+ (App Router)
- **Linguagem:** TypeScript 5.0+
- **Estilização:** Tailwind CSS 3.4+
- **Componentes:** Shadcn/UI (baseado em Radix UI)
- **Gerenciamento de Estado/Dados:** TanStack Query (React Query) v5 para SWR (Stale-While-Revalidate)
- **Ícones:** Lucide React
- **Validação:** Zod + React Hook Form

### 1.2 Backend
- **Linguagem:** Java 21 (LTS)
- **Framework:** Spring Boot 3.2.x
- **Segurança:** Spring Security 6.2+ (JWT)
- **Persistência:** Spring Data JPA
- **Banco de Dados:** SQLite 3
- **Documentação:** SpringDoc OpenAPI (Swagger UI) 2.3.0
- **Build Tool:** Maven 3.9+

---

## 2. Modelagem de Dados (SQLite)

As tabelas serão criadas via Hibernate (Auto-DDL em dev, mas especificadas aqui).

### 2.1 Tabela `users`
| Campo | Tipo | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Identificador único |
| `username` | VARCHAR(50) | UNIQUE, NOT NULL | Nome de usuário para login |
| `password_hash` | VARCHAR(64) | NOT NULL | Hash SHA-256 da senha |
| `is_admin` | BOOLEAN | DEFAULT FALSE | Flag para usuário Master |

### 2.2 Tabela `decks`
| Campo | Tipo | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Identificador único |
| `name` | VARCHAR(100) | NOT NULL | Nome do deck |
| `user_id` | UUID | FK (users.id) | Proprietário do deck |

### 2.3 Tabela `deck_pokemons`
Relacionamento Pokemon-Deck (Um pokemon pode estar em vários decks, mas apenas uma vez por deck).
| Campo | Tipo | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `deck_id` | UUID | FK (decks.id), PK | ID do Deck |
| `pokemon_id` | INTEGER | PK | ID do Pokemon na PokeAPI |
| `added_at` | TIMESTAMP | DEFAULT NOW | Data de adição |

### 2.4 Tabela `gifts` (Presentes/Transferências)
| Campo | Tipo | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Identificador único |
| `sender_id` | UUID | FK (users.id), NOT NULL | Quem enviou |
| `receiver_id` | UUID | FK (users.id), NOT NULL | Quem deve receber |
| `pokemon_id` | INTEGER | NOT NULL | ID do Pokemon na PokeAPI |
| `origin_deck_id` | UUID | FK (decks.id), NOT NULL | Deck de origem do remetente (necessário para devolução em caso de recusa — ver `business-rules-details.md` §4.1) |
| `status` | VARCHAR(20) | NOT NULL, CHECK IN ('PENDING','ACCEPTED','REJECTED') | Status do presente |
| `created_at` | TIMESTAMP | DEFAULT NOW | Data de envio |
| `resolved_at` | TIMESTAMP | NULLABLE | Data de aceite ou recusa |

---

## 3. API Design (Endpoints)

Base URL: `/api/v1`

### 3.1 Autenticação
- `POST /auth/login`: Recebe `{username, password}`. Retorna Token JWT e dados básicos do usuário.

### 3.2 Usuários (Apenas Admin)
- `GET /users`: Lista todos os usuários.
- `POST /users`: Cria novo usuário `{username, password, is_admin}`.
- `DELETE /users/{id}`: Remove um usuário.

### 3.3 Decks
- `GET /decks`: Lista decks do usuário logado.
- `POST /decks`: Cria novo deck `{name}`.
- `DELETE /decks/{id}`: Remove um deck (se pertencer ao usuário).
- `GET /decks/{id}/pokemons`: Lista pokemons de um deck específico (paginado, 4 por vez).

### 3.4 Pokemon (Proxy PokeAPI + Regras de Negócio)
- `GET /pokemons`: Lista pokemons (Proxy da PokeAPI). Deve permitir busca por nome.
- `GET /pokemons/{id}`: Detalhes do pokemon (Fachada: compõe dados da PokeAPI com status de "já possuído no deck selecionado").
- `POST /decks/{deckId}/pokemons`: Adiciona pokemon ao deck. Valida se já existe.

### 3.5 Presentes (Gifts)
- `GET /gifts/pending`: Lista presentes pendentes para o usuário logado.
- `POST /gifts`: Envia um pokemon para outro usuário `{receiver_id, pokemon_id, origin_deck_id}`. O `origin_deck_id` identifica de qual deck do remetente o pokémon será retirado (e para onde deverá voltar em caso de recusa). Operação transacional: remove da tabela `deck_pokemons` e cria registro em `gifts` com `status=PENDING`.
- `PATCH /gifts/{id}`: Aceita ou recusa o presente `{status, deck_id?}`. Se `status=ACCEPTED`, `deck_id` é obrigatório e indica o deck de destino do destinatário. Se `status=REJECTED`, o pokémon é reinserido no `origin_deck_id` do remetente.

---

## 4. Estratégias de Renderização (Frontend)

- **Login:** `Client-Side Rendering` (CSR).
- **Dashboard:** `Server-Side Rendering` (SSR) para carga inicial dos decks + `SWR` (TanStack Query) para navegação entre pokemons dos decks.
- **Catálogo:** `Static Site Generation` (SSG) com `Incremental Static Regeneration` (ISR) a cada 24h para a lista de pokemons, com busca via Client-side.
- **Detalhes:** `SSR` para garantir SEO e dados atualizados da PokeAPI via Backend Proxy.

---

## 5. Docker e Runtime

### 5.1 Backend Dockerfile
- Base: `eclipse-temurin:21-jre-alpine`
- Volume: `/data` (onde ficará o `pokedeck.db`)
- Env: `SPRING_DATASOURCE_URL=jdbc:sqlite:/data/pokedeck.db`

### 5.2 Frontend Dockerfile
- Base: `node:20-alpine` (Build stage) -> `node:20-alpine` (Runner)
- Porta: 3000

---

## 6. Riscos e Observações
- **Persistência SQLite:** Como o Cloud Run é efêmero, a persistência depende de um volume montado ou Cloud Storage FUSE. O detalhamento de infra deve resolver como o arquivo `.db` sobrevive a reinicializações.
- **Limite de Taxa PokeAPI:** O backend deve implementar um cache simples para evitar excesso de chamadas à PokeAPI externa.
- **Segurança:** O hash SHA-256 solicitado em `archicterure.md` será implementado sem salt (conforme requisito), mas recomenda-se cautela.
