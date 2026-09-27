package com.pptnc.pokedeck.service;

import com.pptnc.pokedeck.domain.DeckEntity;
import com.pptnc.pokedeck.domain.DeckPokemonEntity;
import com.pptnc.pokedeck.domain.DeckPokemonId;
import com.pptnc.pokedeck.domain.UserEntity;
import com.pptnc.pokedeck.dto.DeckDTO;
import com.pptnc.pokedeck.dto.PageResponse;
import com.pptnc.pokedeck.dto.PokemonSummaryDTO;
import com.pptnc.pokedeck.repository.DeckPokemonRepository;
import com.pptnc.pokedeck.repository.DeckRepository;
import com.pptnc.pokedeck.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class DeckService {

    private static final Logger log = LoggerFactory.getLogger(DeckService.class);

    private final DeckRepository deckRepository;
    private final DeckPokemonRepository deckPokemonRepository;
    private final UserRepository userRepository;
    private final PokeApiService pokeApiService;
    private final PokemonMetadataResolver pokemonMetadataResolver;

    public DeckService(
        DeckRepository deckRepository,
        DeckPokemonRepository deckPokemonRepository,
        UserRepository userRepository,
        PokeApiService pokeApiService,
        PokemonMetadataResolver pokemonMetadataResolver
    ) {
        this.deckRepository = deckRepository;
        this.deckPokemonRepository = deckPokemonRepository;
        this.userRepository = userRepository;
        this.pokeApiService = pokeApiService;
        this.pokemonMetadataResolver = pokemonMetadataResolver;
    }

    @Transactional(readOnly = true)
    public List<DeckDTO> listForUser(UUID userId) {
        return deckRepository.findAllByOwner_Id(userId.toString()).stream()
            .map(deck -> new DeckDTO(
                deck.getId(),
                deck.getName(),
                (int) deckPokemonRepository.countByIdDeckId(deck.getId().toString())
            ))
            .toList();
    }

    @Transactional
    public DeckDTO create(UUID userId, String name) {
        UserEntity owner = userRepository.findById(userId.toString())
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario nao encontrado"));
        DeckEntity deck = new DeckEntity(UUID.randomUUID(), name, owner);
        DeckEntity saved = deckRepository.save(deck);
        return new DeckDTO(saved.getId(), saved.getName(), 0);
    }

    @Transactional
    public void delete(UUID userId, UUID deckId) {
        DeckEntity deck = loadOwned(userId, deckId);
        deckPokemonRepository.deleteByIdDeckId(deck.getId().toString());
        deckRepository.delete(deck);
    }

    @Transactional(readOnly = true)
    public PageResponse<PokemonSummaryDTO> listPokemons(UUID userId, UUID deckId, int page, int size) {
        DeckEntity deck = loadOwned(userId, deckId);
        int normalizedSize = size <= 0 ? 4 : size;
        int normalizedPage = Math.max(0, page);
        Page<DeckPokemonEntity> result = deckPokemonRepository.findAllByIdDeckIdOrderByAddedAtAsc(
            deck.getId().toString(),
            PageRequest.of(normalizedPage, normalizedSize)
        );
        List<PokemonSummaryDTO> items = result.getContent().stream()
            .map(entry -> resolveSummary(entry.getId().getPokemonId()))
            .toList();
        return PageResponse.of(items, normalizedPage, normalizedSize, result.getTotalElements());
    }

    @Transactional
    public void addPokemon(UUID userId, UUID deckId, int pokemonId) {
        DeckEntity deck = loadOwned(userId, deckId);
        if (deckPokemonRepository.existsByIdDeckIdAndIdPokemonId(deck.getId().toString(), pokemonId)) {
            throw new ResponseStatusException(CONFLICT, "Pokemon ja existe neste deck");
        }
        deckPokemonRepository.save(new DeckPokemonEntity(
            new DeckPokemonId(deck.getId().toString(), pokemonId),
            Instant.now()
        ));
    }

    private PokemonSummaryDTO resolveSummary(int pokemonId) {
        try {
            var detail = pokeApiService.detailById(pokemonId);
            return new PokemonSummaryDTO(
                detail.id(),
                detail.name(),
                detail.imageUrl(),
                detail.types(),
                true
            );
        } catch (RuntimeException ex) {
            log.warn("Falha ao enriquecer pokemon {} via PokeAPI; usando fallback estatico: {}", pokemonId, ex.getMessage());
            return pokemonMetadataResolver.toSummary(pokemonId, true);
        }
    }

    private DeckEntity loadOwned(UUID userId, UUID deckId) {
        DeckEntity deck = deckRepository.findById(deckId.toString())
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Deck nao encontrado"));
        if (!deck.getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(FORBIDDEN, "Deck nao pertence ao usuario logado");
        }
        return deck;
    }
}
