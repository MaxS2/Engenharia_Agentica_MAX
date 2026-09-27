package com.pptnc.pokedeck.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 64)
    private String passwordHash;

    @Column(name = "is_admin", nullable = false)
    private boolean admin;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeckEntity> decks = new ArrayList<>();

    protected UserEntity() {
    }

    public UserEntity(UUID id, String username, String passwordHash, boolean admin) {
        this.id = id.toString();
        this.username = username;
        this.passwordHash = passwordHash;
        this.admin = admin;
    }

    public UUID getId() {
        return UUID.fromString(id);
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public List<DeckEntity> getDecks() {
        return decks;
    }
}
