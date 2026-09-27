package com.pptnc.pokedeck.security;

import com.pptnc.pokedeck.domain.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class AuthenticatedUser implements UserDetails {

    private final UUID id;
    private final String username;
    private final String passwordHash;
    private final boolean admin;

    public AuthenticatedUser(UserEntity entity) {
        this.id = entity.getId();
        this.username = entity.getUsername();
        this.passwordHash = entity.getPasswordHash();
        this.admin = entity.isAdmin();
    }

    public AuthenticatedUser(UUID id, String username, boolean admin) {
        this.id = id;
        this.username = username;
        this.passwordHash = "";
        this.admin = admin;
    }

    public UUID getId() {
        return id;
    }

    public boolean isAdmin() {
        return admin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return admin
            ? List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"))
            : List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
