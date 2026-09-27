package com.pptnc.pokedeck.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Resumo de um pokemon (uso em listas e cards).")
public record PokemonSummaryDTO(
    @Schema(example = "25") int id,
    @Schema(example = "pikachu") String name,
    @Schema(example = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/25.png")
    String imageUrl,
    @Schema(description = "Lista de tipos em PT-BR.", example = "[\"Eletrico\"]")
    List<String> types,
    @Schema(description = "Indica se o pokemon ja pertence ao deck selecionado.", example = "false")
    boolean inSelectedDeck
) {
}
