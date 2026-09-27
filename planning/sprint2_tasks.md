# Tarefas da Sprint 2: Mock Webservice

## 1. Estrutura de Pacotes
- [ ] Criar pacotes: `config`, `controller`, `dto`, `entity`, `repository`, `service`, `security`.

## 2. DTOs (Data Transfer Objects)
- [ ] Criar DTOs para: `LoginRequest`, `AuthResponse`, `UserDTO`, `DeckDTO`, `PokemonDTO`, `GiftDTO`.

## 3. API Design & Controllers (Mock)
- [ ] `AuthController`: Métodos login e logout.
- [ ] `UserController`: CRUD de usuários (exige role ADMIN).
- [ ] `DeckController`: Listar, criar e remover decks.
- [ ] `PokemonController`: Listar catálogo e detalhes (Mockando dados da PokeAPI).
- [ ] `GiftController`: Listar pendentes e processar envio/recebimento.

## 4. Configurações
- [ ] Configurar `OpenApiConfig` para personalizar título e versão no Swagger.
- [ ] Habilitar `WebMvcConfigurer` com suporte a CORS para o domínio do Frontend.
