package com.solgases.infrastructure.api.rest;

import com.solgases.application.dto.InventoryMovementCommand;
import com.solgases.application.dto.InventoryMovementResult;
import com.solgases.application.dto.InventoryResult;
import com.solgases.application.dto.InventoryAdjustmentCommand;
import com.solgases.infrastructure.api.dto.InventoryAdjustmentRequest;
import com.solgases.infrastructure.api.dto.InventoryMovementRequest;
import com.solgases.infrastructure.api.dto.InventoryMovementResponse;
import com.solgases.infrastructure.api.dto.InventoryResponse;

public final class InventoryApiMapper {
    private InventoryApiMapper() {}
    public static InventoryMovementCommand toCommand(InventoryMovementRequest request) {
        return new InventoryMovementCommand(request.quantity(), request.reason(), request.responsibleUser());
    }
    public static InventoryAdjustmentCommand toCommand(InventoryAdjustmentRequest request) {
        return new InventoryAdjustmentCommand(request.quantity(), request.direction(), request.reason(),
                request.responsibleUser());
    }
    public static InventoryMovementResponse toResponse(InventoryMovementResult result) {
        return new InventoryMovementResponse(result.id(), result.productId(), result.type(), result.direction(),
                result.quantity(), result.reason(), result.responsibleUser(), result.movementDate());
    }
    public static InventoryResponse toResponse(InventoryResult result) {
        return new InventoryResponse(result.productId(), result.currentQuantity());
    }
}
