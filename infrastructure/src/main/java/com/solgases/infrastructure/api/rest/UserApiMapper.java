package com.solgases.infrastructure.api.rest;

import com.solgases.application.dto.UserCommand;
import com.solgases.application.dto.UserResult;
import com.solgases.infrastructure.api.dto.UserRequest;
import com.solgases.infrastructure.api.dto.UserResponse;
import java.util.Set;

public final class UserApiMapper {
    private UserApiMapper() {}

    public static UserCommand toCommand(UserRequest request) {
        return new UserCommand(request.username(), request.displayName(), Set.copyOf(request.roleIds()));
    }

    public static UserResponse toResponse(UserResult result) {
        return new UserResponse(result.id(), result.username(), result.displayName(), result.active(),
                result.roles().stream().map(RoleApiMapper::toResponse).toList(),
                result.createdAt(), result.updatedAt());
    }
}
