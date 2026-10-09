package com.solgases.application.usecase;

import com.solgases.application.exception.CategoryNotFoundException;
import com.solgases.application.exception.ConflictException;
import com.solgases.application.dto.ProductCommand;
import com.solgases.application.dto.ProductResult;
import com.solgases.application.exception.DuplicateProductSkuException;
import com.solgases.application.exception.ProductNotFoundException;
import com.solgases.application.port.in.UpdateProductUseCase;
import com.solgases.application.port.out.ProductPersistencePort;
import com.solgases.application.exception.UnitOfMeasureNotFoundException;
import org.springframework.transaction.annotation.Transactional;

public class UpdateProductService implements UpdateProductUseCase {
    private final ProductPersistencePort persistence;
    public UpdateProductService(ProductPersistencePort persistence) { this.persistence = persistence; }
    @Override @Transactional
    public ProductResult execute(Long id, ProductCommand command) {
        var current = persistence.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        if (persistence.existsBySkuAndIdNot(command.sku(), id)) throw new DuplicateProductSkuException(command.sku());
        var category = persistence.findCategoryById(command.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(command.categoryId()));
        if (!category.active()) throw new ConflictException("Category with id " + command.categoryId() + " is not active");
        var unit = persistence.findUnitOfMeasureById(command.unitOfMeasureId())
                .orElseThrow(() -> new UnitOfMeasureNotFoundException(command.unitOfMeasureId()));
        if (!unit.active()) throw new ConflictException("Unit of measure with id " + command.unitOfMeasureId() + " is not active");
        var updated = current.withDetails(command.sku(), command.name(), command.description(), command.brand(),
                command.reference(), command.price(), category, unit);
        return ProductResult.from(persistence.saveChanges(updated));
    }
}
