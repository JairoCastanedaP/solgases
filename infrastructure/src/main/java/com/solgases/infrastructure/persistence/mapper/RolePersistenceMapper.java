package com.solgases.infrastructure.persistence.mapper;

import com.solgases.domain.model.Role;
import com.solgases.infrastructure.persistence.entity.RoleJpaEntity;
import java.util.stream.Collectors;

public final class RolePersistenceMapper {
    private RolePersistenceMapper() {}

    /** Expects the permissions of the role to be already loaded. */
    public static Role toDomain(RoleJpaEntity entity) {
        return new Role(entity.getId(), entity.getKey(), entity.getName(),
                entity.getPermissions().stream().map(PermissionPersistenceMapper::toDomain).collect(Collectors.toSet()),
                entity.getCreatedAt(), entity.getUpdatedAt());
    }
}
