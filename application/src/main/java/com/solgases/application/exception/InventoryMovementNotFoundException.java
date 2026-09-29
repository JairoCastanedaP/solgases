package com.solgases.application.exception;

public class InventoryMovementNotFoundException extends RuntimeException {
    public InventoryMovementNotFoundException(Long movementId, Long productId) {
        super("Movement not found with id " + movementId + " for product " + productId);
    }
}
