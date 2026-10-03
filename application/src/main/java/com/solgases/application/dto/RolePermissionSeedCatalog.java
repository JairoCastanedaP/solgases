package com.solgases.application.dto;

import java.util.List;

/** Initial roles and permissions to load idempotently, identified by their internal keys. */
public record RolePermissionSeedCatalog(List<PermissionSeed> permissions, List<RoleSeed> roles) {

    public RolePermissionSeedCatalog {
        permissions = List.copyOf(permissions);
        roles = List.copyOf(roles);
    }

    public boolean isEmpty() {
        return permissions.isEmpty() && roles.isEmpty();
    }
}
