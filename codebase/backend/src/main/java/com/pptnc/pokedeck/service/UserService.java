package com.pptnc.pokedeck.service;

import com.pptnc.pokedeck.domain.DeckEntity;
import com.pptnc.pokedeck.domain.UserEntity;
import com.pptnc.pokedeck.dto.UserCreateRequest;
import com.pptnc.pokedeck.dto.UserDTO;
import com.pptnc.pokedeck.repository.DeckPokemonRepository;
import com.pptnc.pokedeck.repository.GiftRepository;
import com.pptnc.pokedeck.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class UserService {

    private static final String DEFAULT_DECK_NAME = "Meu primeiro Deck";

    private final UserRepository userRepository;
    private final GiftRepository giftRepository;
    private final DeckPokemonRepository deckPokemonRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
        UserRepository userRepository,
        GiftRepository giftRepository,
        DeckPokemonRepository deckPokemonRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.giftRepository = giftRepository;
        this.deckPokemonRepository = deckPokemonRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserDTO> list() {
        return userRepository.findAll().stream()
            .map(u -> new UserDTO(u.getId(), u.getUsername(), u.isAdmin()))
            .toList();
    }

    @Transactional
    public UserDTO create(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(CONFLICT, "Usuario ja existe: " + request.username());
        }
        UserEntity user = new UserEntity(
            UUID.randomUUID(),
            request.username(),
            passwordEncoder.encode(request.password()),
            request.admin()
        );
        user.getDecks().add(new DeckEntity(UUID.randomUUID(), DEFAULT_DECK_NAME, user));
        try {
            UserEntity saved = userRepository.save(user);
            return new UserDTO(saved.getId(), saved.getUsername(), saved.isAdmin());
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(CONFLICT, "Usuario ja existe: " + request.username());
        }
    }

    @Transactional
    public void delete(UUID id, UUID currentUserId) {
        if (id.equals(currentUserId)) {
            throw new ResponseStatusException(BAD_REQUEST, "Um admin nao pode remover a si mesmo");
        }
        String key = id.toString();
        UserEntity user = userRepository.findById(key)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario nao encontrado"));
        giftRepository.deleteAllBySender_IdOrReceiver_Id(key, key);
        for (DeckEntity deck : user.getDecks()) {
            deckPokemonRepository.deleteByIdDeckId(deck.getId().toString());
        }
        userRepository.delete(user);
    }
}
