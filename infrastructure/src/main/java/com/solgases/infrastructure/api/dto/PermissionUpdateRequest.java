package com.solgases.infrastructure.api.dto;

import com.solgases.domain.model.Permission;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Editable data of a permission. The key cannot be changed.")
public record PermissionUpdateRequest(

        @Schema(description = "Unique, editable permission code", example = "permission.code",
                requiredMode = Schema.RequiredMode.REQUIRED, maxLength = Permission.CODE_MAX_LENGTH)
        @NotBlank
        @Size(max = Permission.CODE_MAX_LENGTH)
        String code) {
}
