package com.pptnc.pokedeck.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estatistica (status) de um pokemon.")
public record PokemonStatDTO(
    @Schema(example = "HP") String label,
    @Schema(example = "35") int baseValue
) {
}
