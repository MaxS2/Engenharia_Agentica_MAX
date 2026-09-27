package com.pptnc.pokedeck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Payload para aceitar ou recusar um presente pendente.")
public record GiftPatchRequest(
    @Schema(description = "ACCEPTED ou REJECTED.") @NotNull GiftStatus status,
    @Schema(description = "Obrigatorio quando status=ACCEPTED: deck de destino do pokemon.")
    UUID deckId
) {
}
