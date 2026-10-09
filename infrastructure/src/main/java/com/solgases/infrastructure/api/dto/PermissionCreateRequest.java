package com.solgases.infrastructure.api.dto;

import com.solgases.domain.model.Permission;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Data to create a permission. The key is set only here and can never change afterwards.")
public record PermissionCreateRequest(

        @Schema(description = "Stable internal key. Unique and immutable once created.", example = "PERMISSION_KEY",
                requiredMode = Schema.RequiredMode.REQUIRED, maxLength = Permission.KEY_MAX_LENGTH)
        @NotBlank
        @Size(max = Permission.KEY_MAX_LENGTH)
        String key,

        @Schema(description = "Unique, editable permission code", example = "permission.code",
                requiredMode = Schema.RequiredMode.REQUIRED, maxLength = Permission.CODE_MAX_LENGTH)
        @NotBlank
        @Size(max = Permission.CODE_MAX_LENGTH)
        String code) {
}
