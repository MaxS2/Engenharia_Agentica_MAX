# Projeto: PPTNC Poke Deck

## Objetivo: Criar uma aplicação web, com alto padrão de qualidade e segurança, para que os usuários possam montar os seus próprios decks de pokemon. A aplicação é multi-usuario e persiste os decks individualmente por usuário.

## Estrutura e Engenharia deste Contexto

	1. Esse é o documento principal e resumo desse projeto.

	2. A estrutura de pastas do projeto é descrita abaixo:
		```
		./project.md // Este documento
		./especification // Diretório das especificações
		./especification/archicterure.md // Setup do Desenvolvimento, Stack e Arquitetura
		./especification/layout.md // Layout e UX
		./especification/business-rules.md // Regras de Negócio
		./especification/infra-devops.md // Deploy e Produção
		./especification/files/ // Arquivos auxiliares
		./planning // Documentos de Evolução do Projeto
		./planning/planning.md // Marcos importantes e validação de entregáveis

		./planning/sprint{n}.md // Escopo e entregáveis de cada Sprint
		./codebase // Onde ficarão os fontes
		./documentation // Local futuro da documentação do projeto
		./tech-debits.md // Débitos Técnicos (se houver)
		./code-review.md // Issues de code review (se houver)
		```
	3. Para cada arquivo de especificação em "/especification" será criado outro documento com o mesmo nome adicional ao prefixo "-details" e mesma extensão .md. É lá onde serão criada a especificação detalhada ao nível de iniciar o desenvolvimento.

	4. Importante: na fase de especificação apenas o detalhamento máximo é desejável. Não iremos produzir código, que será criado posteriormente com base no detalhamento.

	5. Para cada arquivo de sprint na pasta "/planning" você terá mais 2 arquivos adicionais:
		- sprint{n}_tasks.md // Onde serão detalhadas todas as atividades necessárias para realização da sprint
		- sprint{n}_logs.md  // Onde serão documentadas todas as ações realizadas pelo agente naquela sprint.

	6. Nenhuma implementação deve exceder ou faltar o escopo do que está definido em cada sprint da pasta "/planning".

	7. A cada etapa ou criação de arquivo de especificação novo, você deve solicitar a revisão antes de continuar para o próximo.
	
	8. Os documentos na pasta "/especification" e o "/codebase" e os "-logs" das sprints anteriores servem de contexto para para definição das sprints, tasks e escrita do codebase de acordo com a sprint atual.

	9. Pergunte antes de tomar decisões de forma autônoma, especialmente se encontrar situações como essas:
		- Informações conflitantes entre as definições
		- Falta de informação suficiente para realizar a implementação
		- Sugestões de melhorias de performance, especificação ou implementação

	10. Nunca, em hipótese alguma, inicie uma atividade não especificada ou não solicitada. Se for necessário para a realização do trabalho, solicite autorização.

## Dinâmica de agentes

	- Nesse projeto, vamos trabalhar com dois modelos LLM diferentes:
		- Planejador: esse agente vai realizar todas as atividades relacionadas às especificações e planejamento do projeto. Realizará toda as atividades até o início da produção de código.
		- Programador: esse agente vai utilizar todas as atividades do Planejador realizadas anteriormente e iniciar de fato a produção de código, se acordo com as etapas definidas em "planning". Todas as instruções e validações devem ser seguidas a risca.
		- No final do desenvolvimento, o agente planejador irá realizar um code review adversarial para garantir a qualidade do código, além da documentação especificada. Os problemas encontrados deverão ser documentados, mas não alterados.