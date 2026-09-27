package com.pptnc.pokedeck.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "deck_pokemons")
public class DeckPokemonEntity {

    @EmbeddedId
    private DeckPokemonId id;

    @Column(name = "added_at", nullable = false)
    private Instant addedAt;

    protected DeckPokemonEntity() {
    }

    public DeckPokemonEntity(DeckPokemonId id, Instant addedAt) {
        this.id = id;
        this.addedAt = addedAt;
    }

    public DeckPokemonId getId() {
        return id;
    }

    public Instant getAddedAt() {
        return addedAt;
    }
}
