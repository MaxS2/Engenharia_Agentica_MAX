package com.pptnc.pokedeck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload para criacao de um novo deck.")
public record DeckCreateRequest(
    @Schema(example = "Deck dos iniciais") @NotBlank @Size(min = 1, max = 100) String name
) {
}
