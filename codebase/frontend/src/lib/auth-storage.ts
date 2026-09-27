/**
 * Após a Sprint 10 a autenticação é feita exclusivamente via cookie HttpOnly
 * emitido pelo backend. Este módulo mantém apenas utilitários pequenos usados
 * pelo AuthContext (nomes de eventos), sem acesso a localStorage.
 */
export const AUTH_EVENT_EXPIRED = 'pokedeck-auth:expired';
