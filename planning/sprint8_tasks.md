# Tarefas da Sprint 8: Mecanismo de Gift

## 1. Backend: Gifts
- [ ] Implementar `GiftEntity` com `sender_id`, `receiver_id`, `pokemon_id`, `origin_deck_id`.
- [ ] Lógica Transacional: Ao criar Gift, remover do `deck_pokemons` do remetente.
- [ ] Lógica de Aceite: Mover para o novo deck do destinatário.
- [ ] Lógica de Recusa: Mover de volta para o `origin_deck_id`.

## 2. Frontend: Gift Flow
- [ ] Seletor de amigos na tela de detalhes.
- [ ] Implementar interceptor de rota ou check no `layout.tsx` para disparar o `GiftModal` se houver presentes pendentes.
