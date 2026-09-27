# Tarefas da Sprint 4: Autenticação e Gestão

## 1. Backend: Segurança
- [ ] Implementar `UserEntity` com `password_hash` (SHA-256).
- [ ] Criar `CustomUserDetailsService` e `JwtTokenProvider`.
- [ ] Configurar `SecurityFilterChain` para proteger endpoints (permitir apenas Swagger e Login publicamente).
- [ ] Implementar Seeding do usuário `admin/admin` no startup.

## 2. Backend: Gestão de Usuários
- [ ] Implementar logic de criação de usuário (Admin apenas).
- [ ] **Importante:** Garantir que ao criar usuário, o primeiro deck ("Meu primeiro Deck") seja criado.

## 3. Frontend: Integração Auth
- [ ] Configurar `Axios` interceptors para enviar o token JWT no Header.
- [ ] Criar `AuthContext` ou usar `next-auth`/`middleware` para proteger rotas privadas.
- [ ] Implementar formulário de login com validação Zod.
