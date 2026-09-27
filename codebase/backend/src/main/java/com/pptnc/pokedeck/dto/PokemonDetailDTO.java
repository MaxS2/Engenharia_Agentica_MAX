package com.pptnc.pokedeck.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Detalhamento completo de um pokemon (Facade sobre a PokeAPI).")
public record PokemonDetailDTO(
    @Schema(example = "25") int id,
    @Schema(example = "pikachu") String name,
    @Schema(example = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/25.png")
    String imageUrl,
    @Schema(description = "Tipos em PT-BR.", example = "[\"Eletrico\"]")
    List<String> types,
    @Schema(description = "Habilidades em PT-BR.", example = "[\"Estatica\", \"Para-raios\"]")
    List<String> abilities,
    @Schema(description = "Altura em decimetros (padrao PokeAPI).", example = "4") int height,
    @Schema(description = "Peso em hectogramas (padrao PokeAPI).", example = "60") int weight,
    @Schema(description = "Experiencia base.", example = "112") int baseExperience,
    @Schema(description = "Status base.") List<PokemonStatDTO> stats,
    @Schema(description = "Se o pokemon ja pertence ao deck selecionado.", example = "false")
    boolean inSelectedDeck
) {
}
