package com.pptnc.pokedeck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

@Schema(description = "Payload para adicionar um pokemon a um deck.")
public record PokemonAddRequest(
    @Schema(description = "ID do pokemon na PokeAPI.", example = "25")
    @Positive int pokemonId
) {
}
