package com.solgases.category.dto;

import com.solgases.category.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A product category")
public record CategoryResponse(

        @Schema(description = "Generated identifier", example = "1")
        Long id,

        @Schema(description = "Category name", example = "EPP")
        String name,

        @Schema(description = "Whether the category is active", example = "true")
        boolean active) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.isActive());
    }
}
