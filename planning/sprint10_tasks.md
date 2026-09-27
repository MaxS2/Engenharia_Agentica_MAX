# Tarefas da Sprint 10: Hardening

## 1. Segurança: Autenticação Robusta
- [ ] **Backend:** Alterar `AuthController` para retornar o JWT em um Cookie `Set-Cookie` com flags: `HttpOnly`, `Secure`, `SameSite=Strict`, `Path=/`, `Max-Age=86400`.
- [ ] **Backend:** Ajustar `JwtAuthenticationFilter` para extrair o token do Cookie em vez do Header `Authorization`.
- [ ] **Frontend:** Remover toda a lógica de escrita/leitura de `localStorage` em `lib/auth-storage.ts` e `auth-context.tsx`.
- [ ] **Frontend:** Configurar Axios (`services/api.ts`) com `withCredentials: true` para envio automático do cookie.
- [ ] **Frontend:** Atualizar `middleware.ts` para extrair o JWT do cookie e validar sua estrutura/assinatura básica antes de permitir navegação para `(dashboard)`.

## 2. Performance: Otimização de Gifts (Anti N+1)
- [ ] **Backend:** Adicionar campos `pokemonName` e `pokemonImageUrl` na entidade `GiftEntity`.
- [ ] **Backend:** No `GiftService.send`, buscar os metadados da PokeAPI no momento do envio e persistir no banco junto com o presente.
- [ ] **Backend:** Ajustar `GiftDTO` e `GiftService.listPending` para usar os dados denormalizados, eliminando chamadas externas na listagem.

## 3. Integridade: Vault de Devolução
- [ ] **Backend:** No `GiftService.reject`, se o `originDeckId` não existir mais, buscar o deck padrão (ID mais antigo ou nome "Meu Primeiro Deck") do remetente para devolver o pokémon.
- [ ] **Backend:** Adicionar log de auditoria claro quando essa devolução para o "Vault" ocorrer.

## 4. Frontend: UX Mobile
- [ ] **Frontend:** Implementar componente de menu hambúrguer (ex: `Sheet` do shadcn ou similar) que renderize o `AppSidebar` dentro de um drawer lateral em telas pequenas (< 768px).
- [ ] **Frontend:** Adicionar o botão de gatilho (Menu icon) no header do dashboard mobile.

## 5. Finalização
- [ ] **Documentação:** Atualizar `code-review.md` marcando as issues CR-01 a CR-06 como RESOLVIDAS após a implementação.
- [ ] **Build:** Garantir que `npm run build` e `mvn clean package` continuem passando sem erros.
