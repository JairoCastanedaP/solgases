package com.solgases.application.usecase;

import com.solgases.application.exception.ConflictException;
import com.solgases.application.dto.InventoryMovementResult;
import com.solgases.application.port.out.InventoryPersistencePort;
import com.solgases.domain.model.Inventory;
import com.solgases.domain.model.MovementDirection;
import com.solgases.domain.model.MovementType;
import com.solgases.application.exception.ProductNotFoundException;
import java.math.BigDecimal;

final class InventoryMovementRegistration {
    private final InventoryPersistencePort persistence;
    InventoryMovementRegistration(InventoryPersistencePort persistence) { this.persistence = persistence; }
    InventoryMovementResult execute(Long productId, MovementType type, MovementDirection direction,
            BigDecimal quantity, String reason, String responsibleUser) {
        var product = persistence.findProductById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        if (!product.active()) throw new ConflictException("Product with id " + productId + " is not active");
        Inventory inventory = persistence.findByProductId(productId).orElseGet(() -> Inventory.empty(productId));
        BigDecimal newQuantity = direction == MovementDirection.INCREASE
                ? inventory.currentQuantity().add(quantity)
                : inventory.currentQuantity().subtract(quantity);
        if (newQuantity.signum() < 0) {
            throw new ConflictException("Insufficient stock for product " + productId + ": available "
                    + inventory.currentQuantity() + ", requested " + quantity);
        }
        if (newQuantity.compareTo(Inventory.MAX_QUANTITY) > 0) {
            throw new ConflictException("Stock for product " + productId + " would exceed the maximum supported quantity "
                    + Inventory.MAX_QUANTITY.toPlainString() + ": available " + inventory.currentQuantity()
                    + ", requested " + quantity);
        }
        persistence.saveInventory(productId, newQuantity);
        return InventoryMovementResult.from(persistence.saveMovement(
                productId, type, direction, quantity, reason, responsibleUser));
    }
}
