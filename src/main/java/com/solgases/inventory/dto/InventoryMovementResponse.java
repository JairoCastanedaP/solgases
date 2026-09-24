package com.solgases.inventory.dto;

import com.solgases.inventory.entity.InventoryMovement;
import com.solgases.inventory.entity.MovementDirection;
import com.solgases.inventory.entity.MovementType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;

@Schema(description = "A single inventory movement")
public record InventoryMovementResponse(

        @Schema(description = "Generated identifier", example = "42")
        Long id,

        @Schema(description = "Product identifier", example = "1")
        Long productId,

        @Schema(description = "Kind of movement")
        MovementType type,

        @Schema(description = "Effect on stock")
        MovementDirection direction,

        @Schema(description = "Quantity moved", example = "10.000")
        BigDecimal quantity,

        @Schema(description = "Reason for the movement", example = "Compra a proveedor")
        String reason,

        @Schema(description = "Identifier of the person responsible for the movement", example = "jcastaneda")
        String responsibleUser,

        @Schema(description = "Moment the movement took place, assigned by the server")
        Instant movementDate) {

    public static InventoryMovementResponse from(InventoryMovement movement) {
        return new InventoryMovementResponse(
                movement.getId(),
                movement.getProduct().getId(),
                movement.getType(),
                movement.getDirection(),
                movement.getQuantity(),
                movement.getReason(),
                movement.getResponsibleUser(),
                movement.getMovementDate());
    }
}
