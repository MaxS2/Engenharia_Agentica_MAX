package com.pptnc.pokedeck.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Presente (pokemon) enviado entre usuarios.")
public record GiftDTO(
    @Schema(example = "e7c9a0d8-4f2a-4f2c-8f9b-0b9e7c2d1f44") UUID id,
    @Schema(description = "Remetente.") UserDTO sender,
    @Schema(description = "Destinatario.") UserDTO receiver,
    @Schema(description = "ID do pokemon na PokeAPI.", example = "25") int pokemonId,
    @Schema(description = "Nome do pokemon (para exibicao no modal).", example = "pikachu") String pokemonName,
    @Schema(description = "URL da imagem (para exibicao no modal).")
    String pokemonImageUrl,
    @Schema(description = "Deck de origem do remetente (para devolucao em caso de recusa).")
    UUID originDeckId,
    @Schema(description = "Status atual do presente.")
    GiftStatus status,
    @Schema(description = "Data de envio.")
    Instant createdAt,
    @Schema(description = "Data de aceite ou recusa. Nulo enquanto pendente.")
    Instant resolvedAt
) {
}
