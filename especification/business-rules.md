# Projeto: PPTNC Poke Deck

## Resumo: Um Deck simples de coleção de pokemons.

## Mecâninca e funcionamento

    a) Fluxo Comum

        1. A única tela acessível sem login é a própria tela de login. Todas as demais são área-logada.

        2. Dashboard: aqui o usuário pode visualizar os seus decks e os pokemons incluidos em cada um deles. Essa área é paginada e exibe somente 4 pokemons por vez. O usuário pode livremente navegar entre os decks. Ao clicar em um pokemon ele vai para a tela de detalhes.

        3. Detalhes: Aqui o usuário pode somente analisar as características do pokemon e talvez enviar para um amigo.
            - Se escolher enviar para um amigo, ele deve escolher qual usuário do sistema deve receber.
            - Ao logar, o usuário que está recebendo o pokemon deve aceitar ou negar o pokemon.
            - Se aceitar, deve escolher em qual deck vai guarda-lo.

        4. Catálogo: Onde o usuário pode navegar entre todos os pokemons e escolher qual adicionar ao seu deck.
            - Cada pokemon pode ser adicionado somente uma vez.
            - Se o pokemon já existir no deck, ele não deve aparecer como uma opção para o usuário.

    b) Primeiro acesso
        
        1. Cada usuário inicia a sessão com o usuario e senha previamente cadastrado pelo usuário master.

        2. Cada usuário inicia no sistema com o primeiro deck padrão chamado "Meu primeiro Deck", vazio.

    c) Usuario master

        1. Um usuário Master já nasce junto com o sistema.
            - Usuário e senha padrão: admin/admin
        
        2. Só o usuário Master tem acesso a tela de adicionar e remover usuário.

## DETALHAMENTO
    Crie o arquivo business-rules-details.md com todos os pontos descritos aqui em um nível de profundidade adequado para iniciar o desenvolvimento, incluindo querys, modelagem de tabelas e toda informação necessária para início do código. Qualquer dúvida, conflito ou risco deve ser apontado nesse documento.
