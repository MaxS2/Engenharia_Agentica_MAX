# Log da Sprint 9: Polimento e Ajustes Gerais

## 2026-04-22 — Execução

### Backend: Observabilidade
- `config/GlobalExceptionHandler` (`@RestControllerAdvice`) — mapeia:
  - `ResponseStatusException` → repassa status + reason como JSON estruturado.
  - `MethodArgumentNotValidException` → 400 com mensagem concatenada dos campos inválidos.
  - `BadCredentialsException` → 401 "Credenciais invalidas".
  - `AuthenticationException` → 401 "Nao autenticado".
  - `AccessDeniedException` → 403 "Acesso negado".
  - `Exception` (genérico) → 500 + log em `error` com stack trace.
  - Corpo padronizado `{ timestamp, status, error, message }`.
- `resources/logback-spring.xml` — appender `FILE` rolante em `${LOG_DIR:-/data/logs}/app.log`, `SizeAndTimeBasedRollingPolicy` (10MB por arquivo, 14 dias de histórico, 200MB total, arquivos antigos comprimidos em `.log.gz`). Console continua emitindo tudo.

### Backend: Cascade de usuário
- `service/UserService.delete` agora:
  1. Busca o `UserEntity` via `findById`.
  2. Remove todos os gifts onde o user é sender **ou** receiver (`giftRepository.deleteAllBySender_IdOrReceiver_Id`).
  3. Remove `deck_pokemons` de cada deck do user.
  4. `userRepository.delete` (cascade do `OneToMany` dos decks).
- Débito da Sprint 8 fechado.

### Backend: Limpeza
- `service/MockData.java` removido — nenhum controller referencia mais.

### Frontend: Toasts (Sonner)
- `sonner` adicionado como dependência. `<Toaster richColors closeButton position="top-right" />` montado no `app/layout.tsx` (fora do QueryProvider/AuthProvider pra funcionar em qualquer rota).
- `alert()` e `setSubmitError` selecionados foram substituídos por `toast.*`:
  - **Login (`/login`):** mantém `setSubmitError` inline (mais proeminente no card). Toasts seriam ruído.
  - **Dashboard:** `toast.success("Deck ... removido")`, `toast.success("Presente aceito")`, `toast.info("Presente recusado")`, `toast.error(...)` para falhas.
  - **Catálogo:** `toast.success("Pokémon adicionado ao deck")`, `toast.warning("Selecione um deck")`, `toast.error("Já está no deck")`.
  - **Admin:** `toast.success("Usuário criado/removido")`, `toast.error` para falhas de exclusão.
  - **Detalhes:** `toast.success("Presente enviado!")` após o POST /gifts.
  - **CreateDeckDialog:** `toast.success("Deck ... criado")` na mutation.
- `confirm()` mantido onde o fluxo é destrutivo (deletar usuário, deletar deck) — toasts não confirmam, só notificam.

### Frontend: Limpeza de mocks
- `lib/mock-data.ts` removido. Nenhum componente importava mais o arquivo após as Sprints 5/6/8.

### Frontend: Animações
- `app/globals.css` ganhou keyframe `fade-in-up` (240ms ease-out) aplicado em `main` — suaviza a transição ao trocar de rota dentro do route group `(dashboard)`. Sem Framer Motion; apenas CSS, sem custo de bundle.

### README
- `README.md` atualizado com:
  - Manual do usuário master (autenticação, `/admin`, dashboard, catálogo, detalhes, fluxo de gifts).
  - Tabela completa de endpoints principais.
  - Seção de logs mencionando `LOG_DIR` e rotação do Logback.

### Builds validados
- Backend: `mvn clean compile` — BUILD SUCCESS (46 classes).
- Frontend: `npm run build` — 7 rotas OK. `/` cresceu pra 6.61 kB (+Sonner).

### Revisão Adversarial (Check-out do Planejador)
- Realizada análise profunda de segurança, lógica de negócio e performance.
- Identificadas 6 issues críticas documentadas em `code-review.md`.
- Registrados débitos técnicos estruturais em `tech-debits.md`.
- Conclusão: O projeto atende aos requisitos funcionais das 9 sprints, mas requer "hardening" antes de deploy em produção.

### Checkpoints para validação humana
1. Subir `docker compose up --build` e acessar `http://localhost:3000`.
2. Logue com `admin/admin`. Toast "Deck ... criado" ao criar um deck; "Pokémon adicionado" ao adicionar pelo catálogo.
3. Login errado (`admin/xxxx`) → card mostra "Usuário ou senha inválidos" (sem toast, visual inline).
4. Desligue a rede → tente enviar um gift → toast vermelho "Não foi possível enviar o presente" (em vez do antigo `alert`).
5. Em `/admin`, crie um usuário, adicione gifts entre usuários, e depois delete um — o cascade limpa automaticamente (confere via Swagger: `GET /gifts/pending` vazio para ambos os lados).
6. Verifique os arquivos de log: `ls codebase/backend/data/logs/` ou `docker exec pokedeck-backend ls /data/logs` → `app.log` + rotacionados.
7. Navegue entre `/`, `/catalogo`, `/admin` — cada tela aparece com um fade-in suave.

### Observações e débitos técnicos restantes
- **Menu hambúrguer mobile:** o `AppSidebar` ainda fica oculto em viewport <768px (herdou da Sprint 3). Usuário consegue navegar por URL mas sem seletor visual de deck. Não foi incluído no polimento para não explodir o escopo; entra como "próximo passo" se o projeto seguir.
- **Tradução de habilidades PT-BR:** ainda capitalizado a partir do slug EN (ex.: `lightning-rod` → "Lightning rod"). Exigiria fetch extra em `/api/v2/ability/{id}` com cache. Mantido como débito aceitável.
- **SSR da página de detalhes:** continua CSR. Sprint 9 não migrou para server component pra evitar complexidade adicional com JWT em cookie HttpOnly (requer backend suportar cookie, reescrever o interceptor, revalidar CORS). Documentado.
- **`ON DELETE CASCADE` em SQL:** ainda não adicionado no schema — a limpeza é feita na camada de service. Como o SQLite roda sob Hibernate `ddl-auto=update`, não queremos recriar tabelas existentes. Mantido como débito; migrar pro Flyway no futuro.
- **Rate limit PokeAPI:** nenhum throttle explícito (cache em memória basta pro uso atual).

### Resumo geral do projeto
O projeto PPTNC Poke Deck concluiu **nove sprints** alinhadas com a visão spec-driven: das especificações (`especification/`) aos entregáveis funcionais em `codebase/`, com cada sprint validada por checklist humano no respectivo `sprintN_logs.md`. O sistema está estável, documentado e pronto para apresentação.
