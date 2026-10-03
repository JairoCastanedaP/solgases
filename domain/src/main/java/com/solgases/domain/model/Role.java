package com.solgases.domain.model;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A role grouping zero or more permissions. The key is a stable internal identifier that never
 * changes once created; the name is editable.
 */
public record Role(Long id, String key, String name, Set<Permission> permissions, Instant createdAt,
        Instant updatedAt) {

    public static final int KEY_MAX_LENGTH = 50;
    public static final int NAME_MAX_LENGTH = 100;

    public Role {
        permissions = permissions == null ? Set.of() : Set.copyOf(permissions);
    }

    public static Role newRole(String key, String name, Set<Permission> permissions) {
        return new Role(null, key, name, permissions, null, null);
    }

    public Role withDetails(String newName, Set<Permission> newPermissions) {
        return new Role(id, key, newName, newPermissions, createdAt, updatedAt);
    }

    public Set<Long> permissionIds() {
        return permissions.stream().map(Permission::id).collect(Collectors.toUnmodifiableSet());
    }
}
