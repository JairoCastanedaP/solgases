package com.solgases.product.dto;

import com.solgases.category.dto.CategoryResponse;
import com.solgases.product.entity.Product;
import com.solgases.unitofmeasure.dto.UnitOfMeasureResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "A product of the catalog")
public record ProductResponse(

        @Schema(description = "Generated identifier", example = "1")
        Long id,

        @Schema(description = "Business identifier", example = "EPP-CASCO-001")
        String sku,

        @Schema(description = "Product name", example = "Casco de seguridad")
        String name,

        @Schema(description = "Description", example = "Casco de seguridad tipo I, ajuste con cricket")
        String description,

        @Schema(description = "Brand", example = "3M")
        String brand,

        @Schema(description = "Manufacturer reference", example = "H-700")
        String reference,

        @Schema(description = "Sale price", example = "85000.00")
        BigDecimal price,

        @Schema(description = "Whether the product is active", example = "true")
        boolean active,

        @Schema(description = "Category the product belongs to")
        CategoryResponse category,

        @Schema(description = "Unit in which the product is measured")
        UnitOfMeasureResponse unitOfMeasure) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getBrand(),
                product.getReference(),
                product.getPrice(),
                product.isActive(),
                CategoryResponse.from(product.getCategory()),
                UnitOfMeasureResponse.from(product.getUnitOfMeasure()));
    }
}
