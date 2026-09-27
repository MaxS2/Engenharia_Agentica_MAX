# PPTNC Poke Deck

Aplicação web multi-usuário para montar decks de Pokémon. Projeto pedagógico do podcast **PPT Não Compila**.

## Estrutura do repositório

```
.
├── project.md               # Documento mestre
├── especification/          # Especificações (arquitetura, negócio, layout, infra)
├── planning/                # Planejamento de sprints e logs
├── codebase/
│   ├── backend/             # Spring Boot 3.2 + Java 21 + SQLite
│   └── frontend/            # Next.js 14 + TypeScript + Tailwind + Shadcn
├── docker-compose.yml       # Orquestração local
├── cloudbuild.yaml          # Pipeline CI/CD (Cloud Build)
└── .env.example             # Exemplo de variáveis de ambiente
```

## Pré-requisitos

- [Docker](https://www.docker.com/) 24+ com `docker compose`
- Porta **3000** (frontend) e **8080** (backend) livres

## Executando localmente

1. Copie o arquivo de exemplo e ajuste os valores:

   ```bash
   cp .env.example .env
   ```

2. Suba os containers:

   ```bash
   docker compose up --build
   ```

3. Acesse:
   - Frontend: <http://localhost:3000>
   - Backend (Swagger UI): <http://localhost:8080/swagger-ui/index.html>
   - Backend (OpenAPI JSON): <http://localhost:8080/v3/api-docs>

## Usuário master

No primeiro boot o sistema cria automaticamente um usuário administrador:

- **Usuário:** `admin`
- **Senha:** `admin`

Credenciais podem ser sobrescritas via `ADMIN_USERNAME` e `ADMIN_PASSWORD` em `.env`.

## Manual do usuário master

### 1. Autenticação
Acesse `/login`, entre com `admin/admin`. O token JWT é guardado em `localStorage` com validade de 24h.

### 2. Gestão de usuários (`/admin`)
- Listar todos os usuários cadastrados.
- Criar novo usuário (username + senha temporária). Cada novo usuário ganha automaticamente o deck padrão "Meu primeiro Deck".
- Remover usuário (exceto o próprio admin). Apaga em cascata decks, pokémons e gifts relacionados.

### 3. Decks (dashboard `/`)
- Criar deck pelo botão **+** na sidebar.
- Alternar entre decks clicando na sidebar (o badge mostra o número de pokémons).
- Remover deck ativo pelo botão **Remover deck** (operação irreversível).
- Paginação de 4 pokémons por página.

### 4. Catálogo (`/catalogo`)
- Buscar pokémons pela barra (debounce 300 ms) — dados da PokeAPI.
- Selecionar **Deck de destino** e clicar **Adicionar**. Um pokémon já presente no deck selecionado aparece com o selo "Já no deck".

### 5. Detalhes (`/pokemon/{id}`)
- Ficha técnica com imagem de alta resolução, badges de tipo em PT-BR e barras de status.
- **Enviar a um amigo** dispara o fluxo de presente (transacional): remove do deck de origem e cria um `Gift` com status `PENDING`.

### 6. Presentes (fluxo automático)
- Ao logar/voltar ao dashboard, se houver gifts pendentes para o usuário, o **Gift Modal** aparece automaticamente.
- **Aceitar** escolhe um deck de destino e marca `ACCEPTED`.
- **Recusar** devolve o pokémon ao deck de origem do remetente e marca `REJECTED`.

## Endpoints principais

| Método | Endpoint                            | Descrição                                     |
|--------|-------------------------------------|-----------------------------------------------|
| POST   | `/api/v1/auth/login`                | Autentica e devolve JWT                       |
| GET    | `/api/v1/users`                     | Listar usuários (admin)                       |
| POST   | `/api/v1/users`                     | Criar usuário (admin)                         |
| DELETE | `/api/v1/users/{id}`                | Remover usuário (admin)                       |
| GET    | `/api/v1/users/recipients`          | Listar destinatários de gift (autenticado)    |
| GET    | `/api/v1/decks`                     | Listar decks do logado                        |
| POST   | `/api/v1/decks`                     | Criar deck                                    |
| DELETE | `/api/v1/decks/{id}`                | Remover deck                                  |
| GET    | `/api/v1/decks/{id}/pokemons`       | Listar pokémons do deck (paginado, 4/página)  |
| POST   | `/api/v1/decks/{id}/pokemons`       | Adicionar pokémon ao deck                     |
| GET    | `/api/v1/pokemons`                  | Catálogo PokeAPI (`search`, `deckId`, paging) |
| GET    | `/api/v1/pokemons/{id}`             | Detalhes do pokémon                           |
| GET    | `/api/v1/gifts/pending`             | Presentes pendentes para o logado             |
| POST   | `/api/v1/gifts`                     | Enviar pokémon como presente                  |
| PATCH  | `/api/v1/gifts/{id}`                | Aceitar (`ACCEPTED` + `deckId`) ou recusar    |

Swagger completo em `/swagger-ui/index.html`.

## Logs

Em produção, logs em arquivo rodam via Logback (`/data/logs/app.log`, rotação diária, máx. 200 MB). Console continua emitindo tudo. Ajuste `LOG_DIR` se quiser outro caminho.

## Desenvolvimento

- Backend: ver `codebase/backend/README.md`.
- Frontend: ver `codebase/frontend/README.md`.

## Deploy

O deploy é feito via Cloud Build para o Google Cloud Run (projeto `pptnc-stage`, região `us-east1`). Detalhes em `especification/infra-devops-details.md` e `cloudbuild.yaml`.
