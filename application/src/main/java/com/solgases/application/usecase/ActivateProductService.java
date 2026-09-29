package com.solgases.application.usecase;

import com.solgases.application.dto.ProductResult;
import com.solgases.application.exception.ProductNotFoundException;
import com.solgases.application.port.in.ActivateProductUseCase;
import com.solgases.application.port.out.ProductPersistencePort;
import org.springframework.transaction.annotation.Transactional;

public class ActivateProductService implements ActivateProductUseCase {
    private final ProductPersistencePort persistence;
    public ActivateProductService(ProductPersistencePort persistence) { this.persistence = persistence; }
    @Override @Transactional
    public ProductResult execute(Long id) {
        var product = persistence.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        return ProductResult.from(persistence.saveChanges(product.activate()));
    }
}
