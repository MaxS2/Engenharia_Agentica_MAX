# Sprint 2: Mock Webservice

## Objetivo
Desenvolver o esqueleto do Backend (BFF) com todos os endpoints definidos e documentados, retornando dados mocados.

## Escopo
- Definição de Entidades e DTOs.
- Implementação de Controllers com anotações do Swagger/OpenAPI.
- Camada de Service retornando dados estáticos.
- Configuração de CORS para permitir acesso do Frontend.

## Entregáveis
1. **API Documentada:** Swagger UI acessível e funcional.
2. **Endpoints Mockados:** Todos os métodos definidos em `archicterure-details.md` respondendo 200 OK com JSON de exemplo.

## Validação Humana
- [ ] Acessar `/swagger-ui/index.html` e validar se todos os métodos (Auth, Users, Decks, Pokemons, Gifts) estão listados.
- [ ] Testar o endpoint `GET /pokemons` e verificar se retorna uma lista fictícia de pokemons.
