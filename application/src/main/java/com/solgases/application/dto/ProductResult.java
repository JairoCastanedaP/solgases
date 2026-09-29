package com.solgases.application.dto;

import com.solgases.application.dto.CategoryResult;
import com.solgases.domain.model.Product;
import com.solgases.application.dto.UnitOfMeasureResult;
import java.math.BigDecimal;

public record ProductResult(Long id, String sku, String name, String description, String brand, String reference,
        BigDecimal price, boolean active, CategoryResult category, UnitOfMeasureResult unitOfMeasure) {

    public static ProductResult from(Product product) {
        return new ProductResult(product.id(), product.sku(), product.name(), product.description(), product.brand(),
                product.reference(), product.price(), product.active(), CategoryResult.from(product.category()),
                UnitOfMeasureResult.from(product.unitOfMeasure()));
    }
}
