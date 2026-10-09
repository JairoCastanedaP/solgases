package com.solgases.application.usecase;

import com.solgases.application.dto.InventoryMovementResult;
import com.solgases.application.port.in.ListInventoryMovementsUseCase;
import com.solgases.application.port.out.InventoryPersistencePort;
import com.solgases.application.exception.ProductNotFoundException;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public class ListInventoryMovementsService implements ListInventoryMovementsUseCase {
    private final InventoryPersistencePort persistence;
    public ListInventoryMovementsService(InventoryPersistencePort persistence) { this.persistence = persistence; }
    @Override @Transactional(readOnly = true)
    public List<InventoryMovementResult> execute(Long productId) {
        persistence.findProductById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
        return persistence.findMovementsByProductId(productId).stream().map(InventoryMovementResult::from).toList();
    }
}
