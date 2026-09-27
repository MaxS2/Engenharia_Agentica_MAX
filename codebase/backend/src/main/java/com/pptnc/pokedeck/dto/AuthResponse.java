package com.pptnc.pokedeck.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta da autenticacao contendo o token JWT e dados basicos do usuario.")
public record AuthResponse(
    @Schema(description = "Token JWT (Bearer).", example = "eyJhbGciOi...mock...token")
    String token,
    @Schema(description = "Tempo de expiracao em milissegundos.", example = "86400000")
    long expiresInMs,
    @Schema(description = "Dados basicos do usuario autenticado.")
    UserDTO user
) {
}
