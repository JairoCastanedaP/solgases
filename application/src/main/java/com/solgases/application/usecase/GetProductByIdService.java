package com.solgases.application.usecase;

import com.solgases.application.dto.ProductResult;
import com.solgases.application.exception.ProductNotFoundException;
import com.solgases.application.port.in.GetProductByIdUseCase;
import com.solgases.application.port.out.ProductPersistencePort;
import org.springframework.transaction.annotation.Transactional;

public class GetProductByIdService implements GetProductByIdUseCase {
    private final ProductPersistencePort persistence;
    public GetProductByIdService(ProductPersistencePort persistence) { this.persistence = persistence; }
    @Override @Transactional(readOnly = true)
    public ProductResult execute(Long id) {
        return persistence.findById(id).map(ProductResult::from).orElseThrow(() -> new ProductNotFoundException(id));
    }
}
