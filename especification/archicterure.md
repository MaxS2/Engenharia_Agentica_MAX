# Projeto: PPTNC Poke Deck

## Resumo: aplicação web, responsiva. Composta por 2 componentes: um frontend e um webservice como backend. Ambos serão tratados como assets separados.

## Stack

    1. Frontend: React + Next.js + Tailwind + Shadcn/UI
    2. Backend: Java + Springboot + SQLite.

    - Inclua na escificação detalhada as versões mínimas requeridas. Detalhe além das linguagens, as bibliotecas e pacotes necessários para atender o escopo do projeto.
    - Codebase separados na pasta ./codebase:
        - ./codebase/frontend
        - ./codebase/backend

## Runtime
    - Aplicações containerizadas individualmente.
    - Use a imagem base Alpine para reduzir ao máximo o tamanho da imagem docker final.
    - Use somente o que for necessário para o pleno funcionamento da app.
    - Descreva em detalhes todas as caracteristicas necessários para o dockerfile ou dockercompose.
    - Documente o build e run dos containers.

## Comunicação
    - A comunicação entre os assets se dará através de HTTP/Rest Autenticado.

## Segurança
    - Estritamente proibido usuários, senhas, configurações ou endpoints hardcoded no código fonte, qualquer uma dessas informações devem ser tratadas via arquivo de ambiente local (.env) que deverá ser explicatamente ignorado através do .gitignore.
    - Autenticação de usuários: em caso de autenticação de usuários, deverá ser feito em tabela exclusiva no banco de dados, armazenando apenas o hash SHA256 da senha do usuário.
    - Autenticação entre serviços: Backend e Frontend de comunicação via HTTP/Rest, autenticando via JWT. Use spring-security.
    - Detalhe toda e qualquer modelagem de dados que for necessária para autenticação dos usuários, gerenciamento de tokens ou expiricy.

## Observabilidade e Tolerância a falhas
    - Capture e trate e log todos os erros dos níveis WARN e ERROR. INFO devem ser apenas logados.
    - Logs em arquivos text simples, com append, persistidos em I/O.

## Especificidades de cada componente

    1. Frontend:
        - Com base nas regras de negócio e no restante da documentação, defina e detalhe quais são as páginas que deverão ser criadas para esse projeto. Explique a necessidade de cada e qual a melhor estratégia de renderização para cada uma delas: SSG/SSR/ISR/SWR.
        - O frontend não persiste, e não acessa qualquer informação que não esteja disponível no backend.

    2. Backend:
        - Deve expor publicamente uma documentação dos métodos da API através do Swagger/OpenAPI.
        - Deve tratar corretamente a questão de CORS.
        - A persistência será em SQLite, portanto define corretamente a localização do arquivo.db.
            - O container é efemero, logo o arquivo deve estar em um volume persistente. Considere isso ao especificar o dockerfile/compose.
        - O backend atende a todas as necessidades do frontend (BFF), seja fornecendo os dados de sua própria persistência, seja fornecendo dados como proxy de uma API externa.
        - O backend será proxy da API PokeAPI: a documentação está disponível em: https://pokeapi.co/docs/v2. Essa API não requer autenticação.
        - Algumas requisições do frontend podem exigir mais de uma chamada a APIs externas, e/ou composição com dados SQLite. Use o padrão de Facade para esses casos e elimine a complexidade do frontend.
        - Com base nessas informações regras de negócio da aplicação, a documentação da PokeAPI, documente a API Design que será exposta por esse backend. Cada método precisa ser muito bem documentado, com assinatura coerente, objetivo claro e tratando todas as boas práticas arqutieturais de desacoplamento, atomicidade, consistência, isolamento e idempotência.
        - Qualquer persistência de arquivo (imagens, temps, etc) devem ser feitas em diretórios específicos para esse fim, dentro do próprio container efemero.

## DETALHAMENTO
    Crie o arquivo archicterure-details.md com todos os pontos descritos aqui em um nível de profundidade adequado para iniciar o desenvolvimento, incluindo querys, métodos, API Design e toda informação necessária para início do código. Qualquer dúvida, conflito ou risco deve ser apontado nesse documento.