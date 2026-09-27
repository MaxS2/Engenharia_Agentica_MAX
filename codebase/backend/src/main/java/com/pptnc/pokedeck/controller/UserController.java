package com.pptnc.pokedeck.controller;

import com.pptnc.pokedeck.dto.UserCreateRequest;
import com.pptnc.pokedeck.dto.UserDTO;
import com.pptnc.pokedeck.security.AuthenticatedUser;
import com.pptnc.pokedeck.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Tag(name = "Usuarios", description = "Gestao de usuarios (restrito ao admin).")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Lista todos os usuarios. Apenas admin.")
    @GetMapping
    public ResponseEntity<List<UserDTO>> list() {
        return ResponseEntity.ok(userService.list());
    }

    @Operation(summary = "Lista usuarios disponiveis como destinatarios de presentes (exclui o logado). Qualquer autenticado.")
    @GetMapping("/recipients")
    public ResponseEntity<List<UserDTO>> recipients(@AuthenticationPrincipal AuthenticatedUser currentUser) {
        List<UserDTO> recipients = userService.list().stream()
            .filter(u -> !u.id().equals(currentUser.getId()))
            .sorted(Comparator.comparing(UserDTO::username))
            .toList();
        return ResponseEntity.ok(recipients);
    }

    @Operation(summary = "Cria um novo usuario. Apenas admin. Cria automaticamente o deck padrao 'Meu primeiro Deck'.")
    @PostMapping
    public ResponseEntity<UserDTO> create(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.status(201).body(userService.create(request));
    }

    @Operation(summary = "Remove um usuario. Apenas admin; nao pode remover a si mesmo.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @PathVariable UUID id,
        @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        userService.delete(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
