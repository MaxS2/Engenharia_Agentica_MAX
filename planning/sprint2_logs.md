# Log da Sprint 2: Mock Webservice

## 2026-04-17 — Execução

### Pacotes criados
`com.pptnc.pokedeck.config`, `.controller`, `.dto`, `.service` (os pacotes `entity`, `repository` e `security` entrarão a partir da Sprint 4, junto com a persistência e o filtro JWT, conforme `sprint4_tasks.md`).

### Configurações
- `config/CorsConfig.java` — `WebMvcConfigurer` com CORS lendo a property `pokedeck.cors.allowed-origins` (padrão `http://localhost:3000`). Métodos permitidos: GET/POST/PUT/PATCH/DELETE/OPTIONS. Mapeamento `/api/**`.
- `config/OpenApiConfig.java` (já existente da Sprint 1) — personalização do Swagger.

### DTOs (`com.pptnc.pokedeck.dto`)
- Auth: `LoginRequest`, `AuthResponse`.
- Usuário: `UserDTO`, `UserCreateRequest`.
- Deck: `DeckDTO`, `DeckCreateRequest`.
- Pokémon: `PokemonSummaryDTO`, `PokemonStatDTO`, `PokemonDetailDTO`, `PokemonAddRequest`.
- Gift: `GiftStatus` (enum), `GiftDTO`, `GiftCreateRequest`, `GiftPatchRequest`.
- Utilitário: `PageResponse<T>` genérico.
- Todos com `@Schema` para documentação Swagger e validações (`@NotBlank`, `@NotNull`, `@Positive`, `@Size`).

### Mock data
- `service/MockData.java` — classe utilitária com dados estáticos: usuários (admin, ash, misty), dois decks, catálogo de 6 pokémons clássicos, detalhes ricos para `id=1` (bulbasaur) e `id=25` (pikachu), e um gift pendente. Todos os rótulos já em PT-BR (ver `layout-details.md` §3).

### Controllers (todos em `/api/v1`)
| Método | Path | Controller | Observação |
| --- | --- | --- | --- |
| POST | `/auth/login` | `AuthController` | Retorna token fake `mock-jwt-token-<usuario>`. |
| POST | `/auth/logout` | `AuthController` | No-op (JWT stateless), documentado para o frontend. |
| GET | `/users` | `UserController` | Lista mockada; tag indica "apenas admin". |
| POST | `/users` | `UserController` | Retorna 201 + UserDTO mockado. |
| DELETE | `/users/{id}` | `UserController` | Retorna 204. |
| GET | `/decks` | `DeckController` | Lista os decks do usuário logado. |
| POST | `/decks` | `DeckController` | Retorna 201 + novo DeckDTO. |
| DELETE | `/decks/{id}` | `DeckController` | Retorna 204. |
| GET | `/decks/{id}/pokemons` | `DeckController` | Paginado `size=4` (regra §2.1). |
| POST | `/decks/{deckId}/pokemons` | `DeckController` | Retorna 201. |
| GET | `/pokemons` | `PokemonController` | Filtro `search`, paginação e marcação `inSelectedDeck`. |
| GET | `/pokemons/{id}` | `PokemonController` | Facade (mock) PokeAPI + `inSelectedDeck`. |
| GET | `/gifts/pending` | `GiftController` | Lista presentes pendentes do usuário logado. |
| POST | `/gifts` | `GiftController` | Cria presente (recebe `origin_deck_id`). |
| PATCH | `/gifts/{id}` | `GiftController` | Aceita ou recusa. |

Todos os endpoints estão anotados com `@Tag` e `@Operation` para agrupamento e descrição no Swagger.

### Checkpoints para validação humana

O usuário deve executar e conferir:

1. Subir apenas o backend (mais rápido):
   ```bash
   docker compose up --build backend
   ```
   Alternativa local (sem Docker):
   ```bash
   cd codebase/backend && ./mvnw spring-boot:run
   ```
2. Conferir em `http://localhost:8080/swagger-ui/index.html`:
   - [ ] Cinco tags visíveis: **Autenticacao**, **Usuarios**, **Decks**, **Pokemons**, **Presentes**.
   - [ ] Todos os 15 endpoints listados acima aparecem com descrição em PT-BR.
3. Testar via "Try it out":
   - [ ] `GET /api/v1/pokemons` retorna 200 com lista contendo pikachu, bulbasaur, etc.
   - [ ] `GET /api/v1/pokemons/25` retorna detalhes do pikachu com stats em PT-BR ("Ataque", "Velocidade"...).
   - [ ] `POST /api/v1/auth/login` com `{"username":"admin","password":"admin"}` retorna token fake.
4. Conferir CORS: abrir o frontend (porta 3000) não deve gerar erro de CORS ao chamar `GET /api/v1/pokemons` (a validação efetiva desse fluxo é da Sprint 3+).

### Observações e débitos técnicos
- Nenhum endpoint exige autenticação nesta sprint — o `SecurityConfig` continua permissivo. A Sprint 4 adiciona o filtro JWT e restringe `/users/**` ao role ADMIN.
- Mocks retornam dados estáticos; nenhuma persistência em SQLite ocorre (apesar de JPA/SQLite estarem configurados desde a Sprint 1).
- `AuthController#logout` foi incluído por exigência explícita do `sprint2_tasks.md`, mesmo não constando em `archicterure-details.md` §3.1 — fica documentado como no-op para conveniência do frontend.
- `PokemonDetailDTO.inSelectedDeck` e `PokemonSummaryDTO.inSelectedDeck` dependem de `deckId` por query string — query param opcional aceito, mas a lógica real entra na Sprint 6 (quando há persistência).
