package com.pptnc.pokedeck.controller;

import com.pptnc.pokedeck.dto.AuthResponse;
import com.pptnc.pokedeck.dto.LoginRequest;
import com.pptnc.pokedeck.dto.UserDTO;
import com.pptnc.pokedeck.security.AuthenticatedUser;
import com.pptnc.pokedeck.security.JwtCookieProperties;
import com.pptnc.pokedeck.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Tag(name = "Autenticacao", description = "Login, sessao e logout baseados em cookie HttpOnly.")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtCookieProperties cookieProperties;

    public AuthController(
        AuthenticationManager authenticationManager,
        JwtTokenProvider jwtTokenProvider,
        JwtCookieProperties cookieProperties
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.cookieProperties = cookieProperties;
    }

    @Operation(summary = "Autentica e seta cookie HttpOnly de sessao. Devolve dados do usuario.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
            AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
            String token = jwtTokenProvider.generateToken(user);
            UserDTO dto = new UserDTO(user.getId(), user.getUsername(), user.isAdmin());
            ResponseCookie cookie = buildCookie(token, Duration.ofMillis(jwtTokenProvider.getExpirationMs()));
            return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AuthResponse(token, jwtTokenProvider.getExpirationMs(), dto));
        } catch (BadCredentialsException ex) {
            throw new ResponseStatusException(UNAUTHORIZED, "Credenciais invalidas");
        }
    }

    @Operation(summary = "Retorna os dados do usuario autenticado pela sessao (cookie).")
    @GetMapping("/me")
    public ResponseEntity<UserDTO> me(@AuthenticationPrincipal AuthenticatedUser currentUser) {
        if (currentUser == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "Nao autenticado");
        }
        return ResponseEntity.ok(new UserDTO(currentUser.getId(), currentUser.getUsername(), currentUser.isAdmin()));
    }

    @Operation(summary = "Encerra a sessao: limpa o cookie no cliente.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = buildCookie("", Duration.ZERO);
        return ResponseEntity.noContent()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .build();
    }

    private ResponseCookie buildCookie(String value, Duration maxAge) {
        return ResponseCookie.from(cookieProperties.getName(), value)
            .httpOnly(true)
            .secure(cookieProperties.isSecure())
            .sameSite(cookieProperties.getSameSite())
            .path(cookieProperties.getPath())
            .maxAge(maxAge)
            .build();
    }
}
