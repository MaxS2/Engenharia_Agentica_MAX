package com.pptnc.pokedeck.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtTokenProvider(
        @Value("${pokedeck.jwt.secret}") String secret,
        @Value("${pokedeck.jwt.expiration-ms}") long expirationMs
    ) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("pokedeck.jwt.secret precisa ter ao menos 32 bytes");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
    }

    public String generateToken(AuthenticatedUser user) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
            .subject(user.getId().toString())
            .claim("username", user.getUsername())
            .claim("admin", user.isAdmin())
            .issuedAt(now)
            .expiration(expiration)
            .signWith(signingKey)
            .compact();
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    public AuthenticatedUser parse(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();

        UUID id = UUID.fromString(claims.getSubject());
        String username = claims.get("username", String.class);
        Boolean admin = claims.get("admin", Boolean.class);
        return new AuthenticatedUser(id, username, Boolean.TRUE.equals(admin));
    }
}
