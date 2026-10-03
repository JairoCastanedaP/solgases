package com.solgases.domain.model;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An internal user of the system. It holds no password or any other credential.
 */
public record User(Long id, String username, String displayName, boolean active, Set<Role> roles,
        Instant createdAt, Instant updatedAt) {

    public static final int USERNAME_MAX_LENGTH = 50;
    public static final int DISPLAY_NAME_MAX_LENGTH = 100;

    public User {
        roles = roles == null ? Set.of() : Set.copyOf(roles);
    }

    /** Every new user starts active. */
    public static User newUser(String username, String displayName, Set<Role> roles) {
        return new User(null, username, displayName, true, roles, null, null);
    }

    public User withDetails(String newUsername, String newDisplayName, Set<Role> newRoles) {
        return new User(id, newUsername, newDisplayName, active, newRoles, createdAt, updatedAt);
    }

    public User activate() {
        return new User(id, username, displayName, true, roles, createdAt, updatedAt);
    }

    public User deactivate() {
        return new User(id, username, displayName, false, roles, createdAt, updatedAt);
    }

    public Set<Long> roleIds() {
        return roles.stream().map(Role::id).collect(Collectors.toUnmodifiableSet());
    }
}
