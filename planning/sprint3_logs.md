# Log da Sprint 3: Mock Frontend

## 2026-04-17 — Execução

### Assets
- `especification/files/IAra.png` → `codebase/frontend/public/iara.png` (mascote — usado no header da sidebar).
- `especification/files/logo_quadrado.png` → `codebase/frontend/public/logo.png` (logotipo — tela de login e rodapé da sidebar).

### Dependências adicionadas (`codebase/frontend/package.json`)
`@radix-ui/react-avatar`, `react-dialog`, `react-label`, `react-progress`, `react-select`, `react-slot` — primitives para os componentes Shadcn abaixo.

### Componentes shadcn (`src/components/ui/`)
`button`, `card`, `input`, `label`, `progress`, `badge`, `skeleton`, `dialog`, `table`, `avatar`, `select`. Todos baseados nos templates oficiais do Shadcn, com paleta alinhada às CSS vars (`--primary`, `--secondary`, etc). Button ganhou variant extra `success` (verde esmeralda) para o Aceitar do Gift Modal, conforme `layout-details.md` §1.1.

### Helpers e dados mock (`src/lib/` e `src/types/`)
- `types/domain.ts` — tipos TS espelhando os DTOs do backend (User, Deck, PokemonSummary, PokemonDetail, Gift).
- `lib/mock-data.ts` — usuários, decks, lista de pokémons, detalhes ricos de bulbasaur/pikachu e gift pendente.
- `lib/pokemon-types.ts` — mapeamento dos 18 tipos com rótulo PT-BR + cores dos badges (Fogo=vermelho, Agua=azul, Eletrico=amarelo…).
- `lib/format.ts` — `capitalize`, `formatPokemonId` (padrão `#025`).

### Componentes de aplicação (`src/components/`)
- `app-sidebar.tsx` — sidebar fixa (desktop) com avatar IAra, saudação, lista de decks, botão "Novo Deck", link admin (apenas se `user.admin`), link "Sair" e rodapé com logo + copyright.
- `pokemon-card.tsx` — card do Pokémon com imagem PokeAPI, `#ID`, nome capitalizado, badges de tipo coloridos e botão "Ver Detalhes" → `/pokemon/[id]`.
- `pokemon-card-skeleton.tsx` — skeleton loader para a troca de deck.
- `stat-bar.tsx` — linha com label, valor e `Progress`.
- `gift-modal.tsx` — dialog com overlay blur, pokémon recebido, remetente, seletor de deck de destino e botões "Recusar" (outline vermelho) / "Aceitar" (success verde).

### Páginas e rotas
```
src/app/
├── layout.tsx                  (root html, lang=pt-BR)
├── globals.css                 (CSS vars + popover)
├── login/
│   └── page.tsx                (Login centralizado, logo, form)
└── (dashboard)/                (route group — transparente na URL)
    ├── layout.tsx              (flex container com sidebar)
    ├── page.tsx                (Dashboard — sidebar + grid 2x4 com paginação simulada + GiftModal auto-abre)
    ├── pokemon/[id]/page.tsx   (Detalhes — 2 colunas, stats, enviar a amigo)
    └── admin/page.tsx          (Admin — sidebar + tabela de usuários + modal novo usuário)
```

### Checkpoints para validação humana

Subir **apenas** o frontend (mais rápido para validar visual):

```bash
docker compose up --build frontend
```

Ou local (recomendado para dev):

```bash
cd codebase/frontend
npm install
npm run dev
```

Abra `http://localhost:3000` e confira:

- [ ] `/login` — tela centralizada com logo, campos Usuário/Senha e botão Entrar. Ao submeter (qualquer valor), redireciona para `/`.
- [ ] `/` (Dashboard) — sidebar à esquerda com mascote IAra, lista de 3 decks, link "Gerenciar Usuarios" visível (porque o mock é admin). Grid 2x2 com cards de pokémon, ID, nome e badges coloridos por tipo. Paginação Anterior/Próximo.
- [ ] Gift Modal aparece automaticamente no carregamento do Dashboard (simulando fluxo da Sprint 8). Botão "Simular presente" no header reabre o modal.
- [ ] Alternar deck na sidebar dispara o skeleton por ~400ms antes de renderizar os cards (Skeleton loader).
- [ ] Clicar em "Ver Detalhes" de um pokémon vai para `/pokemon/[id]` com imagem grande, badges de tipo, barras de progresso das stats em PT-BR ("Ataque", "Defesa", "Velocidade"...), altura/peso, habilidades e botão "Enviar a um amigo" (abre modal de seleção).
- [ ] `/admin` — tabela com 3 usuários (admin/ash/misty), badges "Admin"/"Usuário", botão Remover (desabilitado para o admin) e modal "Novo usuário".
- [ ] Responsivo: em telas < 768px a sidebar é ocultada (menu hambúrguer foi anotado como débito técnico — ver abaixo).

### Observações e débitos técnicos
- **Hambúrguer mobile** não foi implementado nesta sprint. Em telas < 768px a sidebar some e o usuário não consegue trocar de deck via UI. Impacto visual apenas; funcionalmente todas as rotas continuam acessíveis via URL. Fica como candidato para a Sprint 9 (polimento).
- **Toast (Sonner)** e **animações entre páginas** (ver `layout-details.md` §3) não foram implementadas aqui — entram na Sprint 9.
- Login atualmente redireciona para `/` sem qualquer validação (mock). Fluxo real com JWT entra na Sprint 4.
- Criação de deck, envio real e criação de usuário exibem `alert()` informando a sprint de entrega real. Isso é proposital e será substituído por mutations do TanStack Query.
- Catálogo (`/catalogo`) não existe ainda — Sprint 6.
