package com.solgases.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record Inventory(Long productId, BigDecimal currentQuantity, Instant createdAt, Instant updatedAt) {
    public static Inventory empty(Long productId) { return new Inventory(productId, BigDecimal.ZERO, null, null); }
    public Inventory withQuantity(BigDecimal quantity) { return new Inventory(productId, quantity, createdAt, updatedAt); }
}
