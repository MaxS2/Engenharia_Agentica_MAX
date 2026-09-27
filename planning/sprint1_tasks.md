# Tarefas da Sprint 1: Setup Inicial

Este documento lista as atividades técnicas detalhadas para a execução da Sprint 1.

## 1. Setup Geral e Repositório
- [ ] Criar arquivo `.gitignore` na raiz ignorando `.env`, `node_modules`, `.next`, `target/` e o banco `*.db`.
- [ ] Criar arquivo `docker-compose.yml` na raiz para orquestrar o frontend e backend em modo dev.

## 2. Asset: Backend (Spring Boot)
- [ ] Inicializar projeto Spring Boot em `./codebase/backend` via Spring Initializr:
    - **Project:** Maven
    - **Language:** Java 21
    - **Spring Boot:** 3.2.x
    - **Dependencies:** Spring Web, Spring Security, Spring Data JPA, Validation.
- [ ] Adicionar dependências manuais no `pom.xml`:
    - `org.xerial:sqlite-jdbc`
    - `org.hibernate.orm:hibernate-community-dialects` (Para dialeto SQLite)
    - `org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0`
    - `io.jsonwebtoken:jjwt-api`, `jjwt-impl`, `jjwt-jackson` (Para JWT)
- [ ] Configurar `application.properties`:
    - `spring.datasource.url=jdbc:sqlite:data/pokedeck.db`
    - `spring.jpa.hibernate.ddl-auto=update`
    - `spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect`
- [ ] Criar `Dockerfile` multi-stage (Build: Maven, Run: Alpine JRE 21).

## 3. Asset: Frontend (Next.js)
- [ ] Inicializar projeto Next.js em `./codebase/frontend`:
    - `npx create-next-app@latest . --typescript --tailwind --eslint --app --src-dir --import-alias "@/*"`
- [ ] Instalar dependências de UI e Estado:
    - `npx shadcn-ui@latest init` (Configurar com Slate/Default)
    - `npm install lucide-react @tanstack/react-query zod react-hook-form @hookform/resolvers axios`
- [ ] Criar estrutura de pastas base dentro de `src/`:
    - `components/`, `lib/`, `services/`, `hooks/`, `types/`.
- [ ] Criar `Dockerfile` multi-stage (Build: Node, Run: Node Alpine).

## 4. Infra e CI/CD
- [ ] Criar `cloudbuild.yaml` na raiz com steps para:
    - Build da imagem Docker do Backend.
    - Build da imagem Docker do Frontend.
- [ ] Criar `.env.example` na raiz com as variáveis:
    - `JWT_SECRET=`
    - `NEXT_PUBLIC_API_URL=`
    - `CORS_ALLOWED_ORIGINS=`

## 5. Documentação de Execução
- [ ] Criar `README.md` simples na raiz com instruções de como rodar o `docker-compose up`.
