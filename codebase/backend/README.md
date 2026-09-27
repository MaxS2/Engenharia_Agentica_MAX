# PPTNC Poke Deck — Backend

Backend (BFF) da aplicação PPTNC Poke Deck.

- **Stack:** Java 21 · Spring Boot 3.2 · Spring Security · Spring Data JPA · SQLite · SpringDoc OpenAPI · JJWT
- **Porta padrão:** `8080`
- **Persistência:** SQLite em `/data/pokedeck.db` (dentro do container) ou `data/pokedeck.db` (execução local via Maven)

## Rodar localmente (sem Docker)

```bash
./mvnw spring-boot:run     # ou: mvn spring-boot:run
```

- Swagger UI: <http://localhost:8080/swagger-ui/index.html>
- OpenAPI JSON: <http://localhost:8080/v3/api-docs>

## Rodar via Docker

A partir da raiz do repositório:

```bash
docker compose up --build backend
```

## Variáveis de ambiente

| Variável | Descrição | Padrão |
| --- | --- | --- |
| `SPRING_DATASOURCE_URL` | JDBC URL do SQLite | `jdbc:sqlite:data/pokedeck.db` |
| `JWT_SECRET` | Segredo para assinar o JWT | (obrigatório em produção) |
| `JWT_EXPIRATION` | Expiração do JWT em ms | `86400000` |
| `CORS_ALLOWED_ORIGINS` | Origens permitidas para CORS | `http://localhost:3000` |
| `ADMIN_USERNAME` / `ADMIN_PASSWORD` | Credenciais do seeding do usuário master (Sprint 4) | `admin` / `admin` |

## Status do escopo

Este scaffold cobre apenas a **Sprint 1**: projeto sobe, Swagger responde. Controllers, entidades e segurança JWT serão adicionados nas sprints seguintes, conforme `planning/`.
