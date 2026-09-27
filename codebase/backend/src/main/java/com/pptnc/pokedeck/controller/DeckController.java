package com.pptnc.pokedeck.controller;

import com.pptnc.pokedeck.dto.DeckCreateRequest;
import com.pptnc.pokedeck.dto.DeckDTO;
import com.pptnc.pokedeck.dto.PageResponse;
import com.pptnc.pokedeck.dto.PokemonAddRequest;
import com.pptnc.pokedeck.dto.PokemonSummaryDTO;
import com.pptnc.pokedeck.security.AuthenticatedUser;
import com.pptnc.pokedeck.service.DeckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Decks", description = "Gestao dos decks do usuario logado e listagem paginada dos pokemons do deck.")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/decks")
public class DeckController {

    private final DeckService deckService;

    public DeckController(DeckService deckService) {
        this.deckService = deckService;
    }

    @Operation(summary = "Lista os decks do usuario logado.")
    @GetMapping
    public ResponseEntity<List<DeckDTO>> list(@AuthenticationPrincipal AuthenticatedUser currentUser) {
        return ResponseEntity.ok(deckService.listForUser(currentUser.getId()));
    }

    @Operation(summary = "Cria um novo deck para o usuario logado.")
    @PostMapping
    public ResponseEntity<DeckDTO> create(
        @AuthenticationPrincipal AuthenticatedUser currentUser,
        @Valid @RequestBody DeckCreateRequest request
    ) {
        return ResponseEntity.status(201).body(deckService.create(currentUser.getId(), request.name()));
    }

    @Operation(summary = "Remove um deck do usuario logado.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @AuthenticationPrincipal AuthenticatedUser currentUser,
        @PathVariable UUID id
    ) {
        deckService.delete(currentUser.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Lista os pokemons de um deck, paginados em 4 por pagina (regra de negocio §2.1).")
    @GetMapping("/{id}/pokemons")
    public ResponseEntity<PageResponse<PokemonSummaryDTO>> pokemons(
        @AuthenticationPrincipal AuthenticatedUser currentUser,
        @PathVariable UUID id,
        @Parameter(description = "Pagina 0-indexada.") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Tamanho da pagina. Padrao 4 conforme regra de negocio.") @RequestParam(defaultValue = "4") int size
    ) {
        return ResponseEntity.ok(deckService.listPokemons(currentUser.getId(), id, page, size));
    }

    @Operation(summary = "Adiciona um pokemon ao deck. Rejeita duplicatas (regra de negocio §3.1).")
    @PostMapping("/{deckId}/pokemons")
    public ResponseEntity<Void> addPokemon(
        @AuthenticationPrincipal AuthenticatedUser currentUser,
        @PathVariable UUID deckId,
        @Valid @RequestBody PokemonAddRequest request
    ) {
        deckService.addPokemon(currentUser.getId(), deckId, request.pokemonId());
        return ResponseEntity.status(201).build();
    }
}
