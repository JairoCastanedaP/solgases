package com.solgases.application.dto;

import com.solgases.domain.model.InventoryMovement;
import com.solgases.domain.model.MovementDirection;
import com.solgases.domain.model.MovementType;
import java.math.BigDecimal;
import java.time.Instant;

public record InventoryMovementResult(Long id, Long productId, MovementType type, MovementDirection direction,
        BigDecimal quantity, String reason, String responsibleUser, Instant movementDate) {
    public static InventoryMovementResult from(InventoryMovement movement) {
        return new InventoryMovementResult(movement.id(), movement.productId(), movement.type(), movement.direction(),
                movement.quantity(), movement.reason(), movement.responsibleUser(), movement.movementDate());
    }
}
