package com.pptnc.pokedeck.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Status do presente.")
public enum GiftStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}
