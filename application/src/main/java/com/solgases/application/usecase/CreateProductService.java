package com.solgases.application.usecase;

import com.solgases.application.exception.CategoryNotFoundException;
import com.solgases.application.dto.ProductCommand;
import com.solgases.application.dto.ProductResult;
import com.solgases.application.exception.DuplicateProductSkuException;
import com.solgases.application.port.in.CreateProductUseCase;
import com.solgases.application.port.out.ProductPersistencePort;
import com.solgases.domain.model.Product;
import com.solgases.application.exception.ConflictException;
import com.solgases.application.exception.UnitOfMeasureNotFoundException;
import java.math.BigDecimal;
import org.springframework.transaction.annotation.Transactional;

public class CreateProductService implements CreateProductUseCase {
    private final ProductPersistencePort persistence;
    public CreateProductService(ProductPersistencePort persistence) { this.persistence = persistence; }
    @Override @Transactional
    public ProductResult execute(ProductCommand command) {
        if (persistence.existsBySku(command.sku())) throw new DuplicateProductSkuException(command.sku());
        var category = persistence.findCategoryById(command.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(command.categoryId()));
        if (!category.active()) throw new ConflictException("Category with id " + command.categoryId() + " is not active");
        var unit = persistence.findUnitOfMeasureById(command.unitOfMeasureId())
                .orElseThrow(() -> new UnitOfMeasureNotFoundException(command.unitOfMeasureId()));
        if (!unit.active()) throw new ConflictException("Unit of measure with id " + command.unitOfMeasureId() + " is not active");
        var product = new Product(null, command.sku(), command.name(), command.description(), command.brand(),
                command.reference(), command.price(), true, category, unit);
        return ProductResult.from(persistence.saveNew(product));
    }
}
