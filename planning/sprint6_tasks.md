# Tarefas da Sprint 6: Catálogo

## 1. Backend: Proxy PokeAPI
- [ ] Criar `PokeApiService` usando `RestTemplate` ou `WebClient`.
- [ ] Implementar cache simples (ConcurrentHashMap) para evitar rate limit da PokeAPI.
- [ ] Endpoint `GET /pokemons` com suporte a search e paginação da PokeAPI.

## 2. Backend: Persistência de Pokémons
- [ ] Endpoint `POST /decks/{id}/pokemons` validando duplicidade no SQLite.

## 3. Frontend: Catálogo UI
- [ ] Criar página `/catalogo` com barra de busca.
- [ ] Implementar lógica de "Selecionar Deck de Destino" no catálogo.
