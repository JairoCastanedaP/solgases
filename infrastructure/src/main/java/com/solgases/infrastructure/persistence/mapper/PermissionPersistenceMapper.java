package com.solgases.infrastructure.persistence.mapper;

import com.solgases.domain.model.Permission;
import com.solgases.infrastructure.persistence.entity.PermissionJpaEntity;

public final class PermissionPersistenceMapper {
    private PermissionPersistenceMapper() {}

    public static Permission toDomain(PermissionJpaEntity entity) {
        return new Permission(entity.getId(), entity.getKey(), entity.getCode(), entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
