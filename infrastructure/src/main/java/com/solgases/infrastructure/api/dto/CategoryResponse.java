package com.solgases.infrastructure.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A product category")
public record CategoryResponse(

        @Schema(description = "Generated identifier", example = "1")
        Long id,

        @Schema(description = "Category name", example = "EPP")
        String name,

        @Schema(description = "Whether the category is active", example = "true")
        boolean active) {
}
