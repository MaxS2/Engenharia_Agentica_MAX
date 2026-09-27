package com.pptnc.pokedeck.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DeckPokemonId implements Serializable {

    @Column(name = "deck_id", nullable = false, length = 36)
    private String deckId;

    @Column(name = "pokemon_id", nullable = false)
    private int pokemonId;

    protected DeckPokemonId() {
    }

    public DeckPokemonId(String deckId, int pokemonId) {
        this.deckId = deckId;
        this.pokemonId = pokemonId;
    }

    public String getDeckId() {
        return deckId;
    }

    public int getPokemonId() {
        return pokemonId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DeckPokemonId other)) return false;
        return pokemonId == other.pokemonId && Objects.equals(deckId, other.deckId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deckId, pokemonId);
    }
}
