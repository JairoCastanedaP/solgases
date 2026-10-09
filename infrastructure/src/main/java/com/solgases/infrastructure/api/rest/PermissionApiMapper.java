package com.solgases.infrastructure.api.rest;

import com.solgases.application.dto.PermissionCreateCommand;
import com.solgases.application.dto.PermissionResult;
import com.solgases.application.dto.PermissionUpdateCommand;
import com.solgases.infrastructure.api.dto.PermissionCreateRequest;
import com.solgases.infrastructure.api.dto.PermissionResponse;
import com.solgases.infrastructure.api.dto.PermissionUpdateRequest;

public final class PermissionApiMapper {
    private PermissionApiMapper() {}

    public static PermissionCreateCommand toCommand(PermissionCreateRequest request) {
        return new PermissionCreateCommand(request.key(), request.code());
    }

    public static PermissionUpdateCommand toCommand(PermissionUpdateRequest request) {
        return new PermissionUpdateCommand(request.code());
    }

    public static PermissionResponse toResponse(PermissionResult result) {
        return new PermissionResponse(result.id(), result.key(), result.code(), result.createdAt(), result.updatedAt());
    }
}
