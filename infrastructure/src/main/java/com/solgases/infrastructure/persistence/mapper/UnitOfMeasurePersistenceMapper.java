package com.solgases.infrastructure.persistence.mapper;

import com.solgases.domain.model.UnitOfMeasure;

public final class UnitOfMeasurePersistenceMapper {
    private UnitOfMeasurePersistenceMapper() {}

    public static UnitOfMeasure toDomain(
            com.solgases.infrastructure.persistence.entity.UnitOfMeasureJpaEntity entity) {
        return new UnitOfMeasure(entity.getId(), entity.getCode(), entity.getName(), entity.isActive());
    }

    public static com.solgases.infrastructure.persistence.entity.UnitOfMeasureJpaEntity toNewEntity(
            String code, String name) {
        return new com.solgases.infrastructure.persistence.entity.UnitOfMeasureJpaEntity(code, name);
    }
}
