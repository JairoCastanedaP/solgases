package com.solgases.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record InventoryMovement(Long id, Long productId, MovementType type, MovementDirection direction,
        BigDecimal quantity, String reason, String responsibleUser, Instant movementDate) {
}
