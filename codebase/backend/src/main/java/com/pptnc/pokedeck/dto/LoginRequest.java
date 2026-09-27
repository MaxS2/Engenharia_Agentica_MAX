package com.pptnc.pokedeck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciais para autenticacao.")
public record LoginRequest(
    @Schema(example = "admin") @NotBlank String username,
    @Schema(example = "admin") @NotBlank String password
) {
}
