# Débitos Técnicos - PPTNC Poke Deck

Este documento lista os débitos técnicos identificados que devem ser priorizados em versões futuras do projeto.

## Arquitetura de Backend
1. **Migração para Flyway/Liquibase:** Atualmente o schema é gerado pelo Hibernate (`ddl-auto=update`). É necessário controle de migração versionada.
2. **IDs Binários:** Migrar UUIDs de `String` para `BINARY(16)` no SQLite para melhorar performance de indexação.
3. **Caché de PokeAPI:** Implementar cache local (Caffeine) para os metadados de pokémons, reduzindo o tráfego externo.
4. **Vault de Pokémons:** Criar um mecanismo de "Deck de Recuperação" para evitar a perda de pokémons em fluxos de deleção de decks com presentes pendentes.

## Segurança
1. **Cookie HttpOnly:** Migrar a autenticação JWT para cookies protegidos pelo servidor.
2. **Middleware Auth:** Validar a assinatura do JWT no middleware do Next.js.
3. **Rate Limiting:** Implementar limitador de requisições por IP no Backend para prevenir brute force no `/auth/login`.

## Frontend & UX
1. **Menu Hambúrguer:** Implementar a versão mobile do `AppSidebar`.
2. **Tradução Dinâmica:** Integrar as traduções de habilidades da PokeAPI em vez de usar apenas slugs formatados.
3. **SSR para Detalhes:** Converter a página de detalhes para Server Components para melhorar SEO e performance de carregamento inicial.
