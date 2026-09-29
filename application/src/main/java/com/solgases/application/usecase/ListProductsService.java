package com.solgases.application.usecase;

import com.solgases.application.dto.ProductQuery;
import com.solgases.application.dto.ProductResult;
import com.solgases.application.port.in.ListProductsUseCase;
import com.solgases.application.port.out.ProductPersistencePort;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public class ListProductsService implements ListProductsUseCase {
    private final ProductPersistencePort persistence;
    public ListProductsService(ProductPersistencePort persistence) { this.persistence = persistence; }
    @Override @Transactional(readOnly = true)
    public List<ProductResult> execute(ProductQuery query) {
        return persistence.findAll(query).stream().map(ProductResult::from).toList();
    }
}
