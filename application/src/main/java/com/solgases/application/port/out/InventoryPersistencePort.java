package com.solgases.application.port.out;

import com.solgases.domain.model.Inventory;
import com.solgases.domain.model.InventoryMovement;
import com.solgases.domain.model.MovementDirection;
import com.solgases.domain.model.MovementType;
import com.solgases.domain.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface InventoryPersistencePort {
    Optional<Product> findProductById(Long id);
    Optional<Inventory> findByProductId(Long productId);
    List<InventoryMovement> findMovementsByProductId(Long productId);
    Optional<InventoryMovement> findMovementByIdAndProductId(Long movementId, Long productId);
    Inventory saveInventory(Long productId, BigDecimal quantity);
    InventoryMovement saveMovement(Long productId, MovementType type, MovementDirection direction,
            BigDecimal quantity, String reason, String responsibleUser);
}
