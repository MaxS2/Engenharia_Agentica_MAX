# Sprint 1: Setup Inicial

## Objetivo
Configurar a infraestrutura base do projeto em ambos os assets (Frontend e Backend), garantindo que os ambientes de desenvolvimento, build e deploy estejam operacionais.

## Escopo
- Estruturação do diretório `./codebase`.
- Inicialização do projeto Frontend (Next.js).
- Inicialização do projeto Backend (Spring Boot).
- Configuração de Docker e Docker Compose para desenvolvimento local.
- Definição do pipeline de CI/CD inicial.

## Entregáveis
1.  **Codebase Estruturado:** Pastas `frontend` e `backend` com scaffolds iniciais.
2.  **Ambiente Docker:** `docker-compose.yml` funcional para subir ambos os serviços localmente.
3.  **CI/CD:** Arquivo `cloudbuild.yaml` no diretório raiz.
4.  **Configurações:** Arquivos `.env.example` e `.gitignore` configurados.

## Validação Humana (Checkpoints)
O usuário deve validar:
- [ ] Se o comando `docker-compose up` sobe os dois containers sem erros.
- [ ] Se o projeto Frontend responde na porta 3000 (página padrão do Next.js).
- [ ] Se o projeto Backend responde na porta 8080 (endpoint `/swagger-ui.html` ou `/v3/api-docs`).
- [ ] Se a estrutura de pastas segue rigorosamente o definido em `archicterure-details.md`.

**Após a validação, realizar: commit, tag `v0.1.0-sprint1` e push.**
