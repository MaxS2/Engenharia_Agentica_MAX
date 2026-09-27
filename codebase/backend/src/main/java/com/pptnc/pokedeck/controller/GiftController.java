package com.pptnc.pokedeck.controller;

import com.pptnc.pokedeck.dto.GiftCreateRequest;
import com.pptnc.pokedeck.dto.GiftDTO;
import com.pptnc.pokedeck.dto.GiftPatchRequest;
import com.pptnc.pokedeck.dto.GiftStatus;
import com.pptnc.pokedeck.security.AuthenticatedUser;
import com.pptnc.pokedeck.service.GiftService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Presentes", description = "Envio e recebimento de pokemons entre usuarios.")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/gifts")
public class GiftController {

    private final GiftService giftService;

    public GiftController(GiftService giftService) {
        this.giftService = giftService;
    }

    @Operation(summary = "Lista presentes pendentes (status=PENDING) para o usuario logado.")
    @GetMapping("/pending")
    public ResponseEntity<List<GiftDTO>> pending(@AuthenticationPrincipal AuthenticatedUser currentUser) {
        return ResponseEntity.ok(giftService.listPending(currentUser.getId()));
    }

    @Operation(summary = "Envia um pokemon como presente. Transacional: remove do deck de origem e cria PENDING.")
    @PostMapping
    public ResponseEntity<GiftDTO> send(
        @AuthenticationPrincipal AuthenticatedUser currentUser,
        @Valid @RequestBody GiftCreateRequest request
    ) {
        return ResponseEntity.status(201).body(giftService.send(currentUser.getId(), request));
    }

    @Operation(summary = "Aceita (status=ACCEPTED + deckId) ou recusa (status=REJECTED) um presente.")
    @PatchMapping("/{id}")
    public ResponseEntity<Void> resolve(
        @AuthenticationPrincipal AuthenticatedUser currentUser,
        @PathVariable UUID id,
        @Valid @RequestBody GiftPatchRequest request
    ) {
        if (request.status() == GiftStatus.ACCEPTED) {
            giftService.accept(currentUser.getId(), id, request.deckId());
        } else if (request.status() == GiftStatus.REJECTED) {
            giftService.reject(currentUser.getId(), id);
        } else {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.noContent().build();
    }
}
