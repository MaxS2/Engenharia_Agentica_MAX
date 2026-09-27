package com.pptnc.pokedeck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

@Schema(description = "Payload para envio de um pokemon como presente a outro usuario.")
public record GiftCreateRequest(
    @Schema(description = "ID do usuario destinatario.", example = "e0e1b2c3-d4e5-f6a7-b8c9-d0e1f2a3b4c5")
    @NotNull UUID receiverId,
    @Schema(description = "ID do pokemon na PokeAPI.", example = "25")
    @Positive int pokemonId,
    @Schema(description = "Deck de origem do remetente (de onde o pokemon sera retirado).")
    @NotNull UUID originDeckId
) {
}
