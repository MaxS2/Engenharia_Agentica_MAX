package com.pptnc.pokedeck.service;

import com.pptnc.pokedeck.dto.PokemonSummaryDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Resolve metadados (nome, imagem, tipos) de um pokemon a partir do ID da PokeAPI.
 * Nesta Sprint 5 usamos um mapa estatico dos pokemons conhecidos; a Sprint 7
 * substitui por chamada real ao proxy da PokeAPI.
 */
@Component
public class PokemonMetadataResolver {

    private static final String ARTWORK_TEMPLATE =
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/%d.png";

    private static final Map<Integer, PokemonMetadata> KNOWN = Map.of(
        1, new PokemonMetadata("bulbasaur", List.of("Grama", "Venenoso")),
        4, new PokemonMetadata("charmander", List.of("Fogo")),
        7, new PokemonMetadata("squirtle", List.of("Agua")),
        25, new PokemonMetadata("pikachu", List.of("Eletrico")),
        133, new PokemonMetadata("eevee", List.of("Normal")),
        150, new PokemonMetadata("mewtwo", List.of("Psiquico"))
    );

    public PokemonSummaryDTO toSummary(int pokemonId, boolean inSelectedDeck) {
        PokemonMetadata meta = KNOWN.getOrDefault(pokemonId, new PokemonMetadata("pokemon-" + pokemonId, List.of("Normal")));
        return new PokemonSummaryDTO(
            pokemonId,
            meta.name(),
            ARTWORK_TEMPLATE.formatted(pokemonId),
            meta.types(),
            inSelectedDeck
        );
    }

    private record PokemonMetadata(String name, List<String> types) {
    }
}
