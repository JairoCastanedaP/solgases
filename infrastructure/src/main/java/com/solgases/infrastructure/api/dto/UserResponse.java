package com.solgases.infrastructure.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

@Schema(description = "An internal user with its roles and their permissions")
public record UserResponse(

        @Schema(description = "Generated identifier", example = "1")
        Long id,

        @Schema(description = "Unique username", example = "jcastaneda")
        String username,

        @Schema(description = "Name shown for the user", example = "Jairo Castañeda")
        String displayName,

        @Schema(description = "Whether the user is active", example = "true")
        boolean active,

        @Schema(description = "Roles of the user, ordered by id, each with its permissions. May be empty.")
        List<RoleResponse> roles,

        @Schema(description = "Technical creation timestamp, assigned by the server")
        Instant createdAt,

        @Schema(description = "Technical last-update timestamp, assigned by the server. It also changes when "
                + "the roles of the user change.")
        Instant updatedAt) {
}
