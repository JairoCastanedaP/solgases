package com.solgases.product.dto;

import com.solgases.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Schema(description = "Editable data of a product. The id and the active flag cannot be set by the client. "
        + "Category and unit of measure are referenced by id.")
public record ProductRequest(

        @Schema(description = "Business identifier. Stored exactly as received (no trimming or normalization). Must be unique.",
                example = "EPP-CASCO-001", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = Product.SKU_MAX_LENGTH)
        @NotBlank
        @Size(max = Product.SKU_MAX_LENGTH)
        String sku,

        @Schema(description = "Product name", example = "Casco de seguridad",
                requiredMode = Schema.RequiredMode.REQUIRED, maxLength = Product.NAME_MAX_LENGTH)
        @NotBlank
        @Size(max = Product.NAME_MAX_LENGTH)
        String name,

        @Schema(description = "Optional description", example = "Casco de seguridad tipo I, ajuste con cricket",
                maxLength = Product.DESCRIPTION_MAX_LENGTH)
        @Size(max = Product.DESCRIPTION_MAX_LENGTH)
        String description,

        @Schema(description = "Optional brand", example = "3M", maxLength = Product.BRAND_MAX_LENGTH)
        @Size(max = Product.BRAND_MAX_LENGTH)
        String brand,

        @Schema(description = "Optional manufacturer reference", example = "H-700", maxLength = Product.REFERENCE_MAX_LENGTH)
        @Size(max = Product.REFERENCE_MAX_LENGTH)
        String reference,

        @Schema(description = "Sale price. Must be greater than zero, with at most 2 decimal digits "
                + "(matches the DECIMAL(12,2) column).", example = "85000.00", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Positive
        @Digits(integer = 10, fraction = 2)
        BigDecimal price,

        @Schema(description = "Id of an existing, active category", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        Long categoryId,

        @Schema(description = "Id of an existing, active unit of measure", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        Long unitOfMeasureId) {
}
