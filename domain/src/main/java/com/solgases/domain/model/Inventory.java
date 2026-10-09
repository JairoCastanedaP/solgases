package com.solgases.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record Inventory(Long productId, BigDecimal currentQuantity, Instant createdAt, Instant updatedAt) {
    /** Largest stock that can be stored: the technical maximum of a DECIMAL(15,3) column, not a business limit. */
    public static final BigDecimal MAX_QUANTITY = new BigDecimal("999999999999.999");

    public static Inventory empty(Long productId) { return new Inventory(productId, BigDecimal.ZERO, null, null); }
    public Inventory withQuantity(BigDecimal quantity) { return new Inventory(productId, quantity, createdAt, updatedAt); }
}
