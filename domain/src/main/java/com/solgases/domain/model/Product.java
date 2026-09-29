package com.solgases.domain.model;

import com.solgases.domain.model.Category;
import com.solgases.domain.model.UnitOfMeasure;
import java.math.BigDecimal;

public record Product(Long id, String sku, String name, String description, String brand, String reference,
        BigDecimal price, boolean active, Category category, UnitOfMeasure unitOfMeasure) {

    public static final int SKU_MAX_LENGTH = 50;
    public static final int NAME_MAX_LENGTH = 100;
    public static final int DESCRIPTION_MAX_LENGTH = 255;
    public static final int BRAND_MAX_LENGTH = 255;
    public static final int REFERENCE_MAX_LENGTH = 255;

    public Product withDetails(String sku, String name, String description, String brand, String reference,
            BigDecimal price, Category category, UnitOfMeasure unitOfMeasure) {
        return new Product(id, sku, name, description, brand, reference, price, active, category, unitOfMeasure);
    }

    public Product activate() {
        return new Product(id, sku, name, description, brand, reference, price, true, category, unitOfMeasure);
    }

    public Product deactivate() {
        return new Product(id, sku, name, description, brand, reference, price, false, category, unitOfMeasure);
    }
}
