package com.pptnc.pokedeck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload para criacao de novo usuario (restrito ao admin).")
public record UserCreateRequest(
    @Schema(example = "ash") @NotBlank @Size(min = 3, max = 50) String username,
    @Schema(example = "senha-temporaria-123") @NotBlank @Size(min = 4, max = 100) String password,
    @Schema(description = "Se o novo usuario tambem sera admin.", example = "false") boolean admin
) {
}
