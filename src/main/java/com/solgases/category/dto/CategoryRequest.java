package com.solgases.category.dto;

import com.solgases.category.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Editable data of a category. The id and the active flag cannot be set by the client.")
public record CategoryRequest(

        @Schema(description = "Category name. Stored exactly as received (no trimming or normalization). Must be unique.",
                example = "EPP", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = Category.NAME_MAX_LENGTH)
        @NotBlank
        @Size(max = Category.NAME_MAX_LENGTH)
        String name) {
}
