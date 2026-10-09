package com.solgases.infrastructure.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "A permission")
public record PermissionResponse(

        @Schema(description = "Generated identifier", example = "1")
        Long id,

        @Schema(description = "Stable internal key", example = "PERMISSION_KEY")
        String key,

        @Schema(description = "Editable permission code", example = "permission.code")
        String code,

        @Schema(description = "Technical creation timestamp, assigned by the server")
        Instant createdAt,

        @Schema(description = "Technical last-update timestamp, assigned by the server")
        Instant updatedAt) {
}
