# Detalhamento de Regras de Negócio - PPTNC Poke Deck

Este documento detalha os fluxos lógicos e restrições operacionais da aplicação, expandindo as definições de `business-rules.md`.

## 1. Gestão de Usuários e Acesso

### 1.1 Usuário Master (Admin)
- **Credenciais Iniciais:** `admin` / `admin`.
- **Ação Obrigatória:** O sistema deve garantir que este usuário exista na primeira execução (Seeding).
- **Privilégios:**
    - Visualizar lista de todos os usuários cadastrados.
    - Criar novos usuários (username e senha).
    - Remover usuários existentes (exceto a si mesmo).
    - Nota: O Admin também possui as funcionalidades de usuário comum (ter seus próprios decks).

### 1.2 Primeiro Acesso do Usuário
- Ao ser criado pelo Admin, o usuário não possui decks.
- **Evento de Criação:** No momento da criação do usuário, o sistema deve criar automaticamente um registro na tabela `decks` com o nome "Meu primeiro Deck".
- Este deck inicial deve ser associado ao ID do novo usuário.

---

## 2. Fluxo de Decks e Dashboard

### 2.1 Visualização (Dashboard)
- O Dashboard exibe um deck por vez.
- O usuário pode selecionar qual deck deseja visualizar através de um menu lateral ou seletor.
- **Paginação:** A lista de pokemons dentro de um deck deve ser paginada no backend (limit 4, offset variável).
- Se um deck estiver vazio, exibir um estado de "Empty State" com link para o Catálogo.

### 2.2 Navegação
- Ao clicar em um card de pokemon no Dashboard, o sistema redireciona para a tela de **Detalhes** do pokemon específico.

---

## 3. Catálogo de Pokemons

### 3.1 Regras de Adição
- Um usuário pode navegar por todos os pokemons da PokeAPI.
- **Restrição de Unicidade:** Um pokemon (ID PokeAPI) só pode existir **uma vez** dentro do mesmo deck.
- **Filtro de Exibição:** 
    - No Catálogo, o usuário deve selecionar o "Deck de Destino" antes de adicionar.
    - Se o pokemon já estiver presente no deck selecionado, o botão "Adicionar" deve estar desabilitado ou o card deve exibir um selo "Já no Deck".

### 3.2 Persistência
- A adição ao deck é uma operação atômica no backend, validando a posse do deck pelo usuário logado.

---

## 4. Sistema de Presentes (Gift System)

### 4.1 Envio (Sender)
- Na tela de **Detalhes** (acessada a partir de um deck específico), existe a opção "Enviar a um amigo".
- O usuário deve selecionar um destinatário da lista de usuários ativos (exceto ele mesmo).
- **Transferência de Posse:** Ao enviar, o Pokémon é **imediatamente removido** do deck de origem do remetente. 
- Um registro é criado na tabela `gifts` com o status `PENDING`.
- **Caso de Rejeição:** Se o destinatário recusar o presente, o Pokémon deve ser **devolvido automaticamente** ao deck de origem do remetente.

### 4.2 Recebimento (Receiver)
- Ao realizar login, o sistema verifica a tabela `gifts` por registros com `status = PENDING` para o `receiver_id`.
- Se houver presentes pendentes, o **Gift Modal** deve ser exibido imediatamente após o Dashboard carregar.
- **Ações no Modal:**
    1. **Aceitar:** O usuário deve então escolher em qual de seus decks o Pokémon será salvo. O sistema cria o registro em `deck_pokemons` para o destinatário e atualiza o presente para `status = ACCEPTED`.
    2. **Recusar:** O sistema atualiza o presente para `status = REJECTED` e reestabelece o vínculo do Pokémon com o deck original do remetente.

---

## 5. Integridade e Segurança de Dados

- **Propriedade:** Todas as operações de leitura/escrita em decks e pokemons devem validar se o `user_id` do registro corresponde ao ID do token JWT do usuário logado.
- **Senhas:** Somente o hash SHA-256 é armazenado. O Admin, ao criar um usuário, define uma senha temporária que o usuário usará para o primeiro login.
- **Idempotência:** Requisições de aceitação de presentes devem ser processadas apenas uma vez para evitar duplicatas em caso de múltiplos cliques.

---

## 6. Riscos e Decisões Pendentes
- **Limite de Decks:** Definido inicialmente como ilimitado. Se houver restrição de performance no SQLite, um limite de 10 decks por usuário pode ser implementado.
- **Remoção de Usuário:** Ao remover um usuário, todos os seus decks e registros de `deck_pokemons` associados devem ser removidos (Cascading Delete).
