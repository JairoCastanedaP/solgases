package com.solgases.application.usecase;

import com.solgases.application.dto.InventoryMovementResult;
import com.solgases.application.exception.InventoryMovementNotFoundException;
import com.solgases.application.port.in.GetInventoryMovementUseCase;
import com.solgases.application.port.out.InventoryPersistencePort;
import com.solgases.application.exception.ProductNotFoundException;
import org.springframework.transaction.annotation.Transactional;

public class GetInventoryMovementService implements GetInventoryMovementUseCase {
    private final InventoryPersistencePort persistence;
    public GetInventoryMovementService(InventoryPersistencePort persistence) { this.persistence = persistence; }
    @Override @Transactional(readOnly = true)
    public InventoryMovementResult execute(Long productId, Long movementId) {
        persistence.findProductById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
        return persistence.findMovementByIdAndProductId(movementId, productId).map(InventoryMovementResult::from)
                .orElseThrow(() -> new InventoryMovementNotFoundException(movementId, productId));
    }
}
