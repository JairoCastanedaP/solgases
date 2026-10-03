package com.solgases.infrastructure.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

@Schema(description = "A role with its permissions")
public record RoleResponse(

        @Schema(description = "Generated identifier", example = "1")
        Long id,

        @Schema(description = "Stable internal key", example = "ROLE_KEY")
        String key,

        @Schema(description = "Editable role name", example = "Nombre del rol")
        String name,

        @Schema(description = "Permissions of the role, ordered by id. May be empty.")
        List<PermissionResponse> permissions,

        @Schema(description = "Technical creation timestamp, assigned by the server")
        Instant createdAt,

        @Schema(description = "Technical last-update timestamp, assigned by the server. It also changes when "
                + "the permissions of the role change.")
        Instant updatedAt) {
}
