package com.solgases.application.dto;

import com.solgases.domain.model.Permission;
import java.time.Instant;

public record PermissionResult(Long id, String key, String code, Instant createdAt, Instant updatedAt) {

    public static PermissionResult from(Permission permission) {
        return new PermissionResult(permission.id(), permission.key(), permission.code(), permission.createdAt(),
                permission.updatedAt());
    }
}
