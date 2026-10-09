package com.solgases.application.usecase;

import com.solgases.application.dto.InventoryResult;
import com.solgases.application.port.in.GetInventoryUseCase;
import com.solgases.application.port.out.InventoryPersistencePort;
import com.solgases.application.exception.ProductNotFoundException;
import org.springframework.transaction.annotation.Transactional;

public class GetInventoryService implements GetInventoryUseCase {
    private final InventoryPersistencePort persistence;
    public GetInventoryService(InventoryPersistencePort persistence) { this.persistence = persistence; }
    @Override @Transactional(readOnly = true)
    public InventoryResult execute(Long productId) {
        persistence.findProductById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
        return persistence.findByProductId(productId).map(InventoryResult::from)
                .orElseGet(() -> InventoryResult.zero(productId));
    }
}
