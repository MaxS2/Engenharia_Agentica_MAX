package com.pptnc.pokedeck.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configuração das flags do cookie JWT. Todas são sobrescritíveis via env:
 * - POKEDECK_AUTH_COOKIE_NAME
 * - POKEDECK_AUTH_COOKIE_SECURE
 * - POKEDECK_AUTH_COOKIE_SAMESITE
 * - POKEDECK_AUTH_COOKIE_PATH
 */
@Component
public class JwtCookieProperties {

    private final String name;
    private final boolean secure;
    private final String sameSite;
    private final String path;

    public JwtCookieProperties(
        @Value("${pokedeck.auth.cookie.name:pokedeck-session}") String name,
        @Value("${pokedeck.auth.cookie.secure:false}") boolean secure,
        @Value("${pokedeck.auth.cookie.same-site:Strict}") String sameSite,
        @Value("${pokedeck.auth.cookie.path:/}") String path
    ) {
        this.name = name;
        this.secure = secure;
        this.sameSite = sameSite;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public boolean isSecure() {
        return secure;
    }

    public String getSameSite() {
        return sameSite;
    }

    public String getPath() {
        return path;
    }
}
