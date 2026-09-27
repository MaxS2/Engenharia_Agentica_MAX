package com.pptnc.pokedeck.repository;

import com.pptnc.pokedeck.domain.DeckPokemonEntity;
import com.pptnc.pokedeck.domain.DeckPokemonId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeckPokemonRepository extends JpaRepository<DeckPokemonEntity, DeckPokemonId> {

    Page<DeckPokemonEntity> findAllByIdDeckIdOrderByAddedAtAsc(String deckId, Pageable pageable);

    long countByIdDeckId(String deckId);

    boolean existsByIdDeckIdAndIdPokemonId(String deckId, int pokemonId);

    void deleteByIdDeckId(String deckId);
}
