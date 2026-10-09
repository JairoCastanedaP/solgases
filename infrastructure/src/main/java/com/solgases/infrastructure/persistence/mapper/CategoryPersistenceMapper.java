package com.solgases.infrastructure.persistence.mapper;

import com.solgases.domain.model.Category;

public final class CategoryPersistenceMapper {
    private CategoryPersistenceMapper() {}

    public static Category toDomain(com.solgases.infrastructure.persistence.entity.CategoryJpaEntity entity) {
        return new Category(entity.getId(), entity.getName(), entity.isActive());
    }

    public static com.solgases.infrastructure.persistence.entity.CategoryJpaEntity toNewEntity(String name) {
        return new com.solgases.infrastructure.persistence.entity.CategoryJpaEntity(name);
    }
}
