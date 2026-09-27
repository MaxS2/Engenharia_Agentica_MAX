# Sprint 10: Hardening e Prontidão para Produção

## Resumo: Correção de vulnerabilidades críticas, otimização de performance e ajustes de UX mobile identificados na auditoria adversarial.

Esta sprint foca em transformar o MVP funcional em um sistema robusto e seguro ("Production Ready"), eliminando débitos técnicos de alta gravidade.

## Entregáveis:
1. **Segurança (JWT):** Autenticação via Cookies HttpOnly e proteção de middleware Next.js via validação de assinatura.
2. **Performance (N+1):** Otimização da API de Gifts através de denormalização de metadados.
3. **Integridade de Dados:** Mecanismo de "Vault de Devolução" para evitar a destruição de pokémons em presentes recusados.
4. **UX Mobile:** Implementação do menu de navegação funcional para dispositivos móveis.

## Validação Humana Necessária:
- [ ] Login funcional: Verificar via DevTools (Application > Cookies) que o token JWT não está mais em `localStorage` e sim em um cookie `HttpOnly`.
- [ ] Segurança de Rota: Tentar acessar `/admin` sem o cookie de sessão e garantir redirecionamento imediato para `/login`.
- [ ] Performance de Notificações: Abrir a aba de presentes com múltiplos itens e verificar no Network que não há múltiplas chamadas externas para a PokeAPI no carregamento.
- [ ] Responsividade: Abrir o site no modo mobile (iPhone SE/Pixel 7) e validar que o menu lateral agora é acessível via botão hambúrguer.
