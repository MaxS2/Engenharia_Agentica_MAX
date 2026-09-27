# Projeto: PPTNC Poke Deck

## Resumo: aplicação web com 2 containers. Irá rodar no Google Cloud.

## Repositório
    - Repositório GIT no Github. URL: https://github.com/wellingtoncruz/pptnc-pokedeck
    - Usaremos versionamento semântico {release}.{version}.{build}
    - A versão inicial será 0.1.0. Cada sprint completada significa um incremento no build number.
    - Os dois assets irão ocupar o mesmo repositório.

## Infra de Runtine
    - Cloud Run, sem autenticação. O próprio webservice trata a autenticação por JWT.
    - Zone: us-east1
    - Projeto: pptnc-stage

## Segurança
    - Todos as chamadas a API de ambiente do GCP deve considerar o ADC (Application Default Credencials) já pré configurado para esse ambiente de desenvolvimento. Em produção uma conta de serviço será atribuída para cada asset em produção. Você precisa definir a necessidade de cada conta e os privilégios necessários.

## Build
    - O processo de build e montagem dos containers será realizado via Cloud Build.
    - Os scripts YAML de build precisam levar em consideração, além do build, montagem de container e implantação, a criação de variáveis de ambiente baseados nos arquivo .env locais.

## DETALHAMENTO
    Crie o arquivo infra-devops-details.md com todos os pontos descritos aqui e em todos os demais documentos em um nível de profundidade adequado para iniciar o desenvolvimento do projeto, incluindo todas as instruções para geração de todos os script necessários para o processo de devops como cloudbuild.yaml. Qualquer dúvida, conflito ou risco deve ser apontado nesse documento.