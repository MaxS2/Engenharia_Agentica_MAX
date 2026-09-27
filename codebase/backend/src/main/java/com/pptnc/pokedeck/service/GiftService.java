package com.pptnc.pokedeck.service;

import com.pptnc.pokedeck.domain.DeckEntity;
import com.pptnc.pokedeck.domain.DeckPokemonEntity;
import com.pptnc.pokedeck.domain.DeckPokemonId;
import com.pptnc.pokedeck.domain.GiftEntity;
import com.pptnc.pokedeck.domain.UserEntity;
import com.pptnc.pokedeck.dto.GiftCreateRequest;
import com.pptnc.pokedeck.dto.GiftDTO;
import com.pptnc.pokedeck.dto.GiftStatus;
import com.pptnc.pokedeck.dto.PokemonDetailDTO;
import com.pptnc.pokedeck.dto.UserDTO;
import com.pptnc.pokedeck.repository.DeckPokemonRepository;
import com.pptnc.pokedeck.repository.DeckRepository;
import com.pptnc.pokedeck.repository.GiftRepository;
import com.pptnc.pokedeck.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class GiftService {

    private static final Logger log = LoggerFactory.getLogger(GiftService.class);
    private static final String VAULT_DECK_NAME = "Meu primeiro Deck";
    private static final String ARTWORK_TEMPLATE =
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/%d.png";

    private final GiftRepository giftRepository;
    private final DeckRepository deckRepository;
    private final DeckPokemonRepository deckPokemonRepository;
    private final UserRepository userRepository;
    private final PokeApiService pokeApiService;

    public GiftService(
        GiftRepository giftRepository,
        DeckRepository deckRepository,
        DeckPokemonRepository deckPokemonRepository,
        UserRepository userRepository,
        PokeApiService pokeApiService
    ) {
        this.giftRepository = giftRepository;
        this.deckRepository = deckRepository;
        this.deckPokemonRepository = deckPokemonRepository;
        this.userRepository = userRepository;
        this.pokeApiService = pokeApiService;
    }

    @Transactional(readOnly = true)
    public List<GiftDTO> listPending(UUID receiverId) {
        return giftRepository.findAllByReceiver_IdAndStatusOrderByCreatedAtAsc(
                receiverId.toString(), GiftStatus.PENDING
            ).stream()
            .map(this::toDto)
            .toList();
    }

    @Transactional
    public GiftDTO send(UUID senderId, GiftCreateRequest request) {
        if (senderId.equals(request.receiverId())) {
            throw new ResponseStatusException(BAD_REQUEST, "Nao e possivel enviar pokemon para voce mesmo");
        }
        UserEntity sender = userRepository.findById(senderId.toString())
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Remetente nao encontrado"));
        UserEntity receiver = userRepository.findById(request.receiverId().toString())
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Destinatario nao encontrado"));

        DeckEntity originDeck = deckRepository.findById(request.originDeckId().toString())
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Deck de origem nao encontrado"));
        if (!originDeck.getOwner().getId().equals(senderId)) {
            throw new ResponseStatusException(FORBIDDEN, "Deck de origem nao pertence ao remetente");
        }

        DeckPokemonId id = new DeckPokemonId(originDeck.getId().toString(), request.pokemonId());
        if (!deckPokemonRepository.existsByIdDeckIdAndIdPokemonId(originDeck.getId().toString(), request.pokemonId())) {
            throw new ResponseStatusException(CONFLICT, "Pokemon nao esta no deck de origem");
        }

        PokemonMetadata meta = resolveMetadata(request.pokemonId());
        deckPokemonRepository.deleteById(id);
        GiftEntity gift = new GiftEntity(
            UUID.randomUUID(),
            sender,
            receiver,
            request.pokemonId(),
            meta.name(),
            meta.imageUrl(),
            originDeck.getId(),
            GiftStatus.PENDING,
            Instant.now()
        );
        GiftEntity saved = giftRepository.save(gift);
        return toDto(saved);
    }

    @Transactional
    public void accept(UUID receiverId, UUID giftId, UUID targetDeckId) {
        GiftEntity gift = loadPendingOwnedByReceiver(receiverId, giftId);
        if (targetDeckId == null) {
            throw new ResponseStatusException(BAD_REQUEST, "deckId e obrigatorio para aceitar um presente");
        }
        DeckEntity targetDeck = deckRepository.findById(targetDeckId.toString())
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Deck de destino nao encontrado"));
        if (!targetDeck.getOwner().getId().equals(receiverId)) {
            throw new ResponseStatusException(FORBIDDEN, "Deck de destino nao pertence ao destinatario");
        }
        if (deckPokemonRepository.existsByIdDeckIdAndIdPokemonId(targetDeck.getId().toString(), gift.getPokemonId())) {
            throw new ResponseStatusException(CONFLICT, "Pokemon ja existe no deck de destino");
        }
        deckPokemonRepository.save(new DeckPokemonEntity(
            new DeckPokemonId(targetDeck.getId().toString(), gift.getPokemonId()),
            Instant.now()
        ));
        gift.setStatus(GiftStatus.ACCEPTED);
        gift.setResolvedAt(Instant.now());
    }

    @Transactional
    public void reject(UUID receiverId, UUID giftId) {
        GiftEntity gift = loadPendingOwnedByReceiver(receiverId, giftId);
        DeckEntity targetDeck = resolveReturnDeck(gift);
        String targetDeckId = targetDeck.getId().toString();
        if (!deckPokemonRepository.existsByIdDeckIdAndIdPokemonId(targetDeckId, gift.getPokemonId())) {
            deckPokemonRepository.save(new DeckPokemonEntity(
                new DeckPokemonId(targetDeckId, gift.getPokemonId()),
                Instant.now()
            ));
        }
        gift.setStatus(GiftStatus.REJECTED);
        gift.setResolvedAt(Instant.now());
    }

    /**
     * Vault de Devolução (Sprint 10): determina para qual deck o pokémon recusado retorna.
     * Preferência:
     *   1. Deck de origem original, se ainda existir.
     *   2. Primeiro deck (mais antigo) do remetente.
     *   3. Criar deck padrão "Meu primeiro Deck" se o remetente não tiver nenhum.
     */
    private DeckEntity resolveReturnDeck(GiftEntity gift) {
        String originDeckId = gift.getOriginDeckId().toString();
        return deckRepository.findById(originDeckId)
            .orElseGet(() -> {
                UserEntity sender = gift.getSender();
                List<DeckEntity> decks = deckRepository.findAllByOwner_Id(sender.getId().toString());
                if (!decks.isEmpty()) {
                    DeckEntity fallback = decks.get(0);
                    log.warn(
                        "VAULT: gift {} - deck de origem {} nao existe; devolvendo pokemon {} para deck fallback {} ('{}') do user '{}'",
                        gift.getId(), originDeckId, gift.getPokemonId(), fallback.getId(), fallback.getName(), sender.getUsername()
                    );
                    return fallback;
                }
                DeckEntity vault = new DeckEntity(UUID.randomUUID(), VAULT_DECK_NAME, sender);
                DeckEntity saved = deckRepository.save(vault);
                log.warn(
                    "VAULT: gift {} - user '{}' sem decks; criado deck '{}' (id={}) para devolver pokemon {}",
                    gift.getId(), sender.getUsername(), VAULT_DECK_NAME, saved.getId(), gift.getPokemonId()
                );
                return saved;
            });
    }

    private GiftEntity loadPendingOwnedByReceiver(UUID receiverId, UUID giftId) {
        GiftEntity gift = giftRepository.findById(giftId.toString())
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Presente nao encontrado"));
        if (!gift.getReceiver().getId().equals(receiverId)) {
            throw new ResponseStatusException(FORBIDDEN, "Presente nao pertence a este usuario");
        }
        if (gift.getStatus() != GiftStatus.PENDING) {
            throw new ResponseStatusException(CONFLICT, "Este presente ja foi resolvido");
        }
        return gift;
    }

    private GiftDTO toDto(GiftEntity gift) {
        String name = gift.getPokemonName();
        String imageUrl = gift.getPokemonImageUrl();
        if (name == null || imageUrl == null) {
            PokemonMetadata meta = resolveMetadata(gift.getPokemonId());
            if (name == null) name = meta.name();
            if (imageUrl == null) imageUrl = meta.imageUrl();
        }
        return new GiftDTO(
            gift.getId(),
            toUserDto(gift.getSender()),
            toUserDto(gift.getReceiver()),
            gift.getPokemonId(),
            name,
            imageUrl,
            gift.getOriginDeckId(),
            gift.getStatus(),
            gift.getCreatedAt(),
            gift.getResolvedAt()
        );
    }

    private UserDTO toUserDto(UserEntity entity) {
        return new UserDTO(entity.getId(), entity.getUsername(), entity.isAdmin());
    }

    private PokemonMetadata resolveMetadata(int pokemonId) {
        try {
            PokemonDetailDTO detail = pokeApiService.detailById(pokemonId);
            return new PokemonMetadata(detail.name(), detail.imageUrl());
        } catch (RuntimeException ex) {
            log.warn("Falha ao resolver metadados do pokemon {}: {}", pokemonId, ex.getMessage());
            return new PokemonMetadata("pokemon-" + pokemonId, ARTWORK_TEMPLATE.formatted(pokemonId));
        }
    }

    private record PokemonMetadata(String name, String imageUrl) {
    }
}
