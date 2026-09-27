package com.pptnc.pokedeck.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Envelope generico para respostas paginadas.")
public record PageResponse<T>(
    @Schema(description = "Itens da pagina atual.") List<T> items,
    @Schema(description = "Pagina atual (0-indexada).", example = "0") int page,
    @Schema(description = "Tamanho da pagina.", example = "4") int size,
    @Schema(description = "Total de itens disponiveis.", example = "12") long totalItems,
    @Schema(description = "Total de paginas.", example = "3") int totalPages
) {
    public static <T> PageResponse<T> of(List<T> items, int page, int size, long totalItems) {
        int totalPages = size <= 0 ? 0 : (int) Math.ceil((double) totalItems / size);
        return new PageResponse<>(items, page, size, totalItems, totalPages);
    }
}
