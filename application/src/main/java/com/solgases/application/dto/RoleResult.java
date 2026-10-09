package com.solgases.application.dto;

import com.solgases.domain.model.Role;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

public record RoleResult(Long id, String key, String name, List<PermissionResult> permissions, Instant createdAt,
        Instant updatedAt) {

    public static RoleResult from(Role role) {
        List<PermissionResult> permissions = role.permissions().stream()
                .map(PermissionResult::from)
                .sorted(Comparator.comparing(PermissionResult::id))
                .toList();
        return new RoleResult(role.id(), role.key(), role.name(), permissions, role.createdAt(), role.updatedAt());
    }
}
