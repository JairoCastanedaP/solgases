package com.solgases.infrastructure.persistence.mapper;

import com.solgases.domain.model.Category;
import com.solgases.domain.model.Product;
import com.solgases.domain.model.UnitOfMeasure;

public final class ProductPersistenceMapper {
    private ProductPersistenceMapper() {}

    public static Product toDomain(com.solgases.infrastructure.persistence.entity.ProductJpaEntity entity) {
        Category category = new Category(entity.getCategory().getId(), entity.getCategory().getName(),
                entity.getCategory().isActive());
        UnitOfMeasure unit = new UnitOfMeasure(entity.getUnitOfMeasure().getId(), entity.getUnitOfMeasure().getCode(),
                entity.getUnitOfMeasure().getName(), entity.getUnitOfMeasure().isActive());
        return new Product(entity.getId(), entity.getSku(), entity.getName(), entity.getDescription(), entity.getBrand(),
                entity.getReference(), entity.getPrice(), entity.isActive(), category, unit);
    }
}
