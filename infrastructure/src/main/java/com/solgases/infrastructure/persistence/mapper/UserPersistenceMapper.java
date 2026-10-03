package com.solgases.infrastructure.persistence.mapper;

import com.solgases.domain.model.User;
import com.solgases.infrastructure.persistence.entity.UserJpaEntity;
import java.util.stream.Collectors;

public final class UserPersistenceMapper {
    private UserPersistenceMapper() {}

    /** Expects the roles of the user, and their permissions, to be already loaded. */
    public static User toDomain(UserJpaEntity entity) {
        return new User(entity.getId(), entity.getUsername(), entity.getDisplayName(), entity.isActive(),
                entity.getRoles().stream().map(RolePersistenceMapper::toDomain).collect(Collectors.toSet()),
                entity.getCreatedAt(), entity.getUpdatedAt());
    }
}
