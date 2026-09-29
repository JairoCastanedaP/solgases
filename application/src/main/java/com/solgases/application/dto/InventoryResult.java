package com.solgases.application.dto;

import com.solgases.domain.model.Inventory;
import java.math.BigDecimal;

public record InventoryResult(Long productId, BigDecimal currentQuantity) {
    public static InventoryResult from(Inventory inventory) {
        return new InventoryResult(inventory.productId(), inventory.currentQuantity());
    }
    public static InventoryResult zero(Long productId) { return new InventoryResult(productId, BigDecimal.ZERO); }
}
