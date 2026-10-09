package com.solgases.infrastructure.api.rest;

import com.solgases.application.dto.RoleCreateCommand;
import com.solgases.application.dto.RoleResult;
import com.solgases.application.dto.RoleUpdateCommand;
import com.solgases.infrastructure.api.dto.RoleCreateRequest;
import com.solgases.infrastructure.api.dto.RoleResponse;
import com.solgases.infrastructure.api.dto.RoleUpdateRequest;
import java.util.Set;

public final class RoleApiMapper {
    private RoleApiMapper() {}

    public static RoleCreateCommand toCommand(RoleCreateRequest request) {
        return new RoleCreateCommand(request.key(), request.name(), Set.copyOf(request.permissionIds()));
    }

    public static RoleUpdateCommand toCommand(RoleUpdateRequest request) {
        return new RoleUpdateCommand(request.name(), Set.copyOf(request.permissionIds()));
    }

    public static RoleResponse toResponse(RoleResult result) {
        return new RoleResponse(result.id(), result.key(), result.name(),
                result.permissions().stream().map(PermissionApiMapper::toResponse).toList(),
                result.createdAt(), result.updatedAt());
    }
}
