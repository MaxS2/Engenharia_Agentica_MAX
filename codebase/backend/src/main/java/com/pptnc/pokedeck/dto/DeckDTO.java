package com.pptnc.pokedeck.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Deck de um usuario.")
public record DeckDTO(
    @Schema(example = "b5a5f0c0-9d3b-4d0f-9bdc-5f1f0d2a8a10") UUID id,
    @Schema(example = "Meu primeiro Deck") String name,
    @Schema(description = "Quantidade de pokemons no deck.", example = "4") int pokemonCount
) {
}
