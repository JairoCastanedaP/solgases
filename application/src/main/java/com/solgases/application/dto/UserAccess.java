package com.solgases.application.dto;

import java.util.Set;

/**
 * Current access state of a user, resolved on every protected request: whether it is active and the stable
 * internal keys of the permissions granted through its roles.
 */
public record UserAccess(Long userId, boolean active, Set<String> permissionKeys) {

    public UserAccess {
        permissionKeys = permissionKeys == null ? Set.of() : Set.copyOf(permissionKeys);
    }
}
