package com.pptnc.pokedeck.domain;

import com.pptnc.pokedeck.dto.GiftStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "gifts")
public class GiftEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private UserEntity sender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    private UserEntity receiver;

    @Column(name = "pokemon_id", nullable = false)
    private int pokemonId;

    @Column(name = "pokemon_name", length = 100)
    private String pokemonName;

    @Column(name = "pokemon_image_url", length = 500)
    private String pokemonImageUrl;

    @Column(name = "origin_deck_id", nullable = false, length = 36)
    private String originDeckId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private GiftStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    protected GiftEntity() {
    }

    public GiftEntity(
        UUID id,
        UserEntity sender,
        UserEntity receiver,
        int pokemonId,
        String pokemonName,
        String pokemonImageUrl,
        UUID originDeckId,
        GiftStatus status,
        Instant createdAt
    ) {
        this.id = id.toString();
        this.sender = sender;
        this.receiver = receiver;
        this.pokemonId = pokemonId;
        this.pokemonName = pokemonName;
        this.pokemonImageUrl = pokemonImageUrl;
        this.originDeckId = originDeckId.toString();
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return UUID.fromString(id);
    }

    public UserEntity getSender() {
        return sender;
    }

    public UserEntity getReceiver() {
        return receiver;
    }

    public int getPokemonId() {
        return pokemonId;
    }

    public String getPokemonName() {
        return pokemonName;
    }

    public String getPokemonImageUrl() {
        return pokemonImageUrl;
    }

    public UUID getOriginDeckId() {
        return UUID.fromString(originDeckId);
    }

    public GiftStatus getStatus() {
        return status;
    }

    public void setStatus(GiftStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(Instant resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}
