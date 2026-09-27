package com.pptnc.pokedeck.controller;

import com.pptnc.pokedeck.dto.PageResponse;
import com.pptnc.pokedeck.dto.PokemonDetailDTO;
import com.pptnc.pokedeck.dto.PokemonSummaryDTO;
import com.pptnc.pokedeck.repository.DeckPokemonRepository;
import com.pptnc.pokedeck.service.PokeApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Pokemons", description = "Proxy da PokeAPI com regras de negocio (fachada).")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/pokemons")
public class PokemonController {

    private final PokeApiService pokeApiService;
    private final DeckPokemonRepository deckPokemonRepository;

    public PokemonController(PokeApiService pokeApiService, DeckPokemonRepository deckPokemonRepository) {
        this.pokeApiService = pokeApiService;
        this.deckPokemonRepository = deckPokemonRepository;
    }

    @Operation(summary = "Lista pokemons da PokeAPI (paginado), com filtro por nome e marcacao 'ja no deck' quando deckId e informado.")
    @GetMapping
    public ResponseEntity<PageResponse<PokemonSummaryDTO>> list(
        @Parameter(description = "Filtro parcial de nome.") @RequestParam(required = false) String search,
        @Parameter(description = "Deck considerado para calcular 'inSelectedDeck'.") @RequestParam(required = false) UUID deckId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        PageResponse<PokemonSummaryDTO> result = pokeApiService.search(search, page, size);
        if (deckId == null) {
            return ResponseEntity.ok(result);
        }
        String deckIdStr = deckId.toString();
        var decorated = result.items().stream()
            .map(summary -> new PokemonSummaryDTO(
                summary.id(),
                summary.name(),
                summary.imageUrl(),
                summary.types(),
                deckPokemonRepository.existsByIdDeckIdAndIdPokemonId(deckIdStr, summary.id())
            ))
            .toList();
        return ResponseEntity.ok(new PageResponse<>(decorated, result.page(), result.size(), result.totalItems(), result.totalPages()));
    }

    @Operation(summary = "Detalhes de um pokemon (fachada PokeAPI + status 'ja no deck').")
    @GetMapping("/{id}")
    public ResponseEntity<PokemonDetailDTO> detail(
        @PathVariable int id,
        @Parameter(description = "Deck considerado para calcular 'inSelectedDeck'.") @RequestParam(required = false) UUID deckId
    ) {
        PokemonDetailDTO base = pokeApiService.detailById(id);
        boolean inDeck = deckId != null
            && deckPokemonRepository.existsByIdDeckIdAndIdPokemonId(deckId.toString(), id);
        if (!inDeck && deckId == null) {
            return ResponseEntity.ok(base);
        }
        return ResponseEntity.ok(new PokemonDetailDTO(
            base.id(),
            base.name(),
            base.imageUrl(),
            base.types(),
            base.abilities(),
            base.height(),
            base.weight(),
            base.baseExperience(),
            base.stats(),
            inDeck
        ));
    }
}
