package com.solgases.inventory.dto;

import com.solgases.inventory.entity.Inventory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "The current stock of a product")
public record InventoryResponse(

        @Schema(description = "Product identifier", example = "1")
        Long productId,

        @Schema(description = "Current available quantity. Zero if the product has no movement yet.",
                example = "120.000")
        BigDecimal currentQuantity) {

    public static InventoryResponse from(Inventory inventory) {
        return new InventoryResponse(inventory.getProduct().getId(), inventory.getCurrentQuantity());
    }

    public static InventoryResponse zero(Long productId) {
        return new InventoryResponse(productId, BigDecimal.ZERO);
    }
}
