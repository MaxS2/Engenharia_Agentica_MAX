# Projeto: PPTNC Poke Deck

## Resumo: Resumo das fases de entrega do projeto com checkpoints de validação humana.

Em cada arquivo de sprint, você deve necessáriamente detalhas minisciosamente quais validações humanas precisam ser feitas para validar a entrega e então partir para a próxima sprint.

** Importante: ** Realizar um commit, tag e push to remote do projeto após a validação de cada sprint.

## Sprints:

    1. Setup inicial: Arquivos de configuração, instalação de frameworks, criação de dockerfiles e setup de CI/CD.
        - Entregável: codebase inicial, repositório configurado e ambiente pronto para desenvolvimento.

    2. Mock Webservice: Asset do webservice (BFF) com toda a documentação Swagger entregue, com métodos mockado (sem lógica real). O endpoint deve ser deployável.
        - Entregável: build & e run do webservice para analise e conferência humana dos endpoints.

    3. Mock Frontend: Asset do frontend com as telas protótipo de todas as áreas da app, ainda sem autenticação.
        - Entregável: telas visualmente fieis ao resultado final para validação e a ajustes humanos.

    4. Autenticação e Gestão: Telas internas protegidas por login.
        - Entregáveis:
            - Todo o mecanismo de autenticação pronto e testável.
            - App possível de login com o usuário admin.
            - Tela de gestão dos usuários pronta e funcional.

    5. Dashsboard: Tela de visualização e administração dos decks.
        - Entregáveis:
            - Visualização dos decks do usuário (ainda que vazios)
            - Todo a funcionalidade de criar e remover decks completa e funcional.
             
    6. Catálogo: Seleção e adição de pokemons ao catálogo.
        - Entregáveis:
            - Navegação no catálogo, visualizando os pokemons e podendo adiciona-los ao deck.
            - Visualização do Deck com os pokemons adicionados.

    7. Detalhes: Tela de visualização de todas as informações minusciosas de cada pokemon do deck.
        - Entregáveis:
            - Tela de visualização e conteplação de cada pokemon na visão do colecionador, com todas as informações disponíveis na API.

    8. Mecanismo de Gift: Todo mecanismo de enviar pokemon a outro usuário conforme detalhado em outros documentos.
        - Entregáveis: todo o sistema de envio de pokemons pronto e testável.

    9. Polimento e ajustes gerais.
        - Entregável: eventuais ajustes e aperfeiçoamentos que não tenham sido citados nas sprints anteriores.

    10. Hardening e Prontidão para Produção (Pós-Revisão Adversarial).
        - Entregáveis:
            - Autenticação migrada para Cookies HttpOnly.
            - Resolução de N+1 na API de Gifts.
            - Mecanismo de Vault para integridade de dados.
            - Menu Mobile funcional.

    ## DETALHAMENTO
Planejador: Crie os arquivos de planejamento de sprint conforme definido em documento anterior. Faça isso de forma iterativa com o humano, declarando sua visão sobre cada sprint e aguardando a confirmação antes da criação do arquivo. Essa é a última especificação antes do desenvolvedor, então seu detalhamento precisa ser minucioso no nível de desenvolvimento para que não haja interpretação vaga das tasks e implementação que serão realizadas. Todo o contexto refinado aqui é importante.
Programador: Siga o desenvolvimento passo a passo com o humano. No final de cada sprint, você deve instrui-lo de como validar por conta própria os entregáveis de cada sprint.
