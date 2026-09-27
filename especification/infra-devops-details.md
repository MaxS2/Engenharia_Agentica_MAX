# Detalhamento de Infraestrutura e DevOps - PPTNC Poke Deck

Este documento detalha o processo de CI/CD, deployment e configuração da infraestrutura no Google Cloud Platform (GCP), expandindo as definições de `infra-devops.md`.

## 1. Arquitetura de Implantação (Cloud Run)

A aplicação será dividida em dois serviços independentes rodando no Cloud Run, na região `us-east1`, dentro do projeto `pptnc-stage`.

### 1.1 Serviço Frontend (`frontend-service`)
- **Porta:** 3000
- **Acesso:** Público (Allow unauthenticated invocations).
- **Variáveis de Ambiente Necessárias:**
  - `NEXT_PUBLIC_API_URL`: URL base do backend (ex: `https://backend-service-xyz-ue.a.run.app/api/v1`).

### 1.2 Serviço Backend (`backend-service`)
- **Porta:** 8080 (padrão Spring Boot / Cloud Run).
- **Acesso:** Público (o controle de acesso é feito via JWT no próprio código).
- **Variáveis de Ambiente Necessárias:**
  - `SPRING_DATASOURCE_URL`: String de conexão do banco de dados.
  - `JWT_SECRET`: Chave secreta para assinatura dos tokens.
  - `JWT_EXPIRATION`: Tempo de expiração (ex: `86400000` para 24h).
  - `CORS_ALLOWED_ORIGINS`: URL do frontend (ex: `https://frontend-service-xyz-ue.a.run.app`).

---

## 2. Persistência de Dados (SQLite no Cloud Run)

O Cloud Run possui um sistema de arquivos efêmero. Como a especificação exige SQLite, o banco de dados será perdido a cada reinicialização do container se não houver um mecanismo de persistência.

**Solução Definida:** Cloud Storage FUSE (Volume Mount).
- Será criado um bucket no Cloud Storage: `gs://pptnc-stage-pokedeck-db`.
- O Cloud Run será configurado para montar este bucket como um volume de sistema de arquivos local (ex: no diretório `/data`).
- O `SPRING_DATASOURCE_URL` apontará para `jdbc:sqlite:/data/pokedeck.db`.

*Nota de Risco:* O SQLite com Cloud Storage FUSE pode ter problemas de locking se houver múltiplas instâncias do Cloud Run tentando escrever simultaneamente. Para este ambiente "Stage", a concorrência do Cloud Run será limitada a **1 instância máxima** (`--max-instances=1`) para garantir a integridade do banco de dados SQLite.

---

## 3. Segurança e Service Accounts

- Será criada uma Service Account dedicada: `pokedeck-runner@pptnc-stage.iam.gserviceaccount.com`.
- **Permissões (Roles):**
  - `roles/storage.objectAdmin` (Para o Cloud Storage FUSE poder ler/escrever o arquivo SQLite).
  - (Opcional) `roles/cloudtrace.agent` e `roles/logging.logWriter` para observabilidade.
- O ADC (Application Default Credentials) utilizará esta Service Account em produção.

---

## 4. Pipeline de CI/CD (Cloud Build)

O repositório no GitHub será conectado ao Cloud Build. Serão criados gatilhos (triggers) baseados em push na branch principal (ou tags de release).

### 4.1 Script `cloudbuild.yaml` (Visão Geral)
O processo de build será dividido em duas etapas paralelas (ou sequenciais) para Backend e Frontend:

**Passos Backend:**
1. Construir a imagem Docker do Backend (a partir do `codebase/backend/Dockerfile`).
2. Fazer push da imagem para o Artifact Registry.
3. Fazer o deploy no Cloud Run (serviço backend), mapeando o volume do Cloud Storage FUSE e configurando as variáveis de ambiente baseadas no `.env` do GCP Secret Manager ou substituições do Cloud Build.

**Passos Frontend:**
1. Construir a imagem Docker do Frontend (a partir do `codebase/frontend/Dockerfile`). O build do Next.js precisará da URL do backend já definida (idealmente via argumento de build ou variável de ambiente em tempo de execução).
2. Fazer push da imagem para o Artifact Registry.
3. Fazer o deploy no Cloud Run (serviço frontend).

---

## 5. Versionamento

- Versão inicial: `0.1.0`.
- O build number será controlado por tags no Git ou pelo ID de execução do Cloud Build (`$BUILD_ID`).

---

## 6. Observabilidade

- Os logs gerados em arquivos texto simples com append (requisito do `archicterure.md`) serão salvos no mesmo volume persistente montado (`/data/logs/app.log`), ou diretamente para a saída padrão (`stdout`/`stderr`) para serem capturados automaticamente pelo Cloud Logging, o que é a prática recomendada no GCP. 
- *Recomendação:* Utilizar `stdout`/`stderr` com formatação JSON para melhor integração com o Cloud Logging, mantendo o fallback de arquivo no diretório `/data` caso estritamente necessário pelo escopo da disciplina.