package com.pptnc.pokedeck.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Usuario do sistema. A senha nao e exposta.")
public record UserDTO(
    @Schema(example = "0e3c6f5a-2a37-4c7b-8f88-3f9d7d2a5c11") UUID id,
    @Schema(example = "admin") String username,
    @Schema(description = "True se for usuario master (admin).", example = "true") boolean admin
) {
}
