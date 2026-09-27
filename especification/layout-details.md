# Detalhamento de Layout e UX - PPTNC Poke Deck

Este documento detalha a interface e a experiência do usuário, expandindo as definições de `layout.md`.

## 1. Identidade Visual e Guia de Estilo

### 1.1 Paleta de Cores (Inspirada no PPTNC)
- **Primária (Brand):** `#2563EB` (Azul Vibrante) - Botões principais, links e headers.
- **Secundária:** `#7C3AED` (Roxo) - Destaques do Mascote IAra e elementos interativos.
- **Sucesso/Aceite:** `#10B981` (Verde Esmeralda) - Botão "Aceitar" e indicações de posse.
- **Erro/Recusa:** `#EF4444` (Vermelho) - Botão "Recusar" e mensagens de erro.
- **Fundo:** `#F8FAFC` (Cinza muito claro) - Para a área de trabalho.
- **Cards:** `#FFFFFF` (Branco) com bordas suaves.

### 1.2 Tipografia
- **Principal:** `Inter` ou `Geist` (Sans-serif moderna).
- **Títulos:** Semibold para hierarquia clara.
- **Interface:** Tamanho padrão 14px/16px para legibilidade.

### 1.3 Componentes (Shadcn/UI)
- **Botões:** `Variant: default` para ações principais, `outline` para secundárias.
- **Cards:** Com `shadow-sm` e `hover:shadow-md` para interatividade.
- **Inputs:** Com labels claros e validação visual em tempo real.

---

## 2. Detalhamento das Telas

### 2.1 Tela de Login (`/login`)
- **Layout:** Centralizado (Hero pattern).
- **Elementos:**
    - Logotipo (`logo_quadrado.png`) centralizado acima do card.
    - Card de login com campos "Usuário" e "Senha".
    - Botão "Entrar" ocupando toda a largura do card.
    - Rodapé discreto: "PPT Não Compila © 2026".

### 2.2 Dashboard (`/`)
- **Estrutura:** Layout com Sidebar fixa à esquerda e conteúdo fluido à direita.
- **Sidebar:**
    - Cabeçalho com o Mascote `IAra.png` em tamanho pequeno (Avatar style).
    - Lista de Decks do usuário com botão "➕ Novo Deck" no topo.
    - Link "Gerenciar Usuários" (visível apenas para Admin) na base.
- **Conteúdo Principal:**
    - Título do Deck selecionado em destaque.
    - Grid de Pokémons: Exibição de 4 cards grandes (2x2 em telas médias, 1x1 em mobile).
    - **Card de Pokémon:**
        - Imagem oficial do Pokémon (high-res).
        - Nome (Capitalized).
        - Tipos (Selo visual com cores correspondentes: Fogo=Vermelho, Água=Azul, etc).
        - Botão "Ver Detalhes".
    - Paginação: Controles de "Anterior" e "Próximo" abaixo do grid.

### 2.3 Detalhes do Pokémon (`/pokemon/[id]`)
- **Layout:** Duas colunas em desktop.
- **Coluna Esq:** Imagem grande do Pokémon com fundo gradiente leve.
- **Coluna Dir:**
    - Nome e ID.
    - Status (HP, Ataque, Defesa) usando `Progress Bars`.
    - Habilidades e Experiência.
- **Ações:**
    - Botão "Enviar a um amigo" (Abre seletor de usuários).
    - Botão "Voltar ao Deck".

### 2.4 Gift Modal (Intersticial)
- **Trigger:** Logo após o carregamento do Dashboard se `gifts.pending > 0`.
- **Visual:**
    - Overlay escurecido (`backdrop-blur`).
    - Mensagem: "Você recebeu um presente de [Nome do Usuário]!"
    - Imagem do Pokémon sendo recebido.
    - Seletor de "Para qual deck enviar?".
    - Botões lado a lado: "Recusar" (Outline) e "Aceitar" (Success).

### 2.5 Admin Panel (`/admin/users`)
- **Visual:** Tabela limpa com colunas: Username, Admin (Badge), Ações.
- **Formulário:** Modal para "Adicionar Novo Usuário".

---

## 3. Comportamento e UX (Microinterações)

- **Loading States:** Usar `Skeleton Screens` da Shadcn/UI para os cards de Pokémon enquanto a PokeAPI responde.
- **Feedback:** Toast notifications (Sonner) para: "Pokémon adicionado!", "Presente enviado!", "Usuário criado!".
- **Responsividade:** Mobile-first. Sidebar vira menu "Hamburger" em telas menores.
- **Linguagem:** 100% PT-BR (Ex: "Ataque" em vez de "Attack", "Defesa" em vez de "Defense").

---

## 4. Descrição das Prévias (Referência para Implementação)

*Devido a limitações técnicas, as imagens `previa{n}.png` não podem ser geradas fisicamente, mas aqui estão os descritivos para o desenvolvedor frontend:*

- **`previa1.png` (Login):** Fundo cinza claro, card branco centralizado, logo azul no topo, botões azuis.
- **`previa2.png` (Dashboard):** Sidebar azul marinho, itens da lista em branco. Cards de pokémon com bordas arredondadas e badges coloridos para os tipos.
- **`previa3.png` (Details):** Layout limpo, foco total na arte do pokémon à esquerda e estatísticas técnicas em barras de progresso à direita.
- **`previa4.png` (Gift Modal):** Modal central com efeito de vidro (glassmorphism), destacando a pergunta "Aceitar presente?" em destaque.
