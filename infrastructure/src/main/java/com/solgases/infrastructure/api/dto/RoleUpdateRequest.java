package com.solgases.infrastructure.api.dto;

import com.solgases.domain.model.Role;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

@Schema(description = "Editable data of a role. The key cannot be changed.")
public record RoleUpdateRequest(

        @Schema(description = "Unique, editable role name", example = "Nombre del rol",
                requiredMode = Schema.RequiredMode.REQUIRED, maxLength = Role.NAME_MAX_LENGTH)
        @NotBlank
        @Size(max = Role.NAME_MAX_LENGTH)
        String name,

        @ArraySchema(arraySchema = @Schema(description = "Ids of existing permissions. Replaces the current "
                + "permissions; an empty list removes them all.", requiredMode = Schema.RequiredMode.REQUIRED),
                schema = @Schema(example = "1"))
        @NotNull
        Set<@NotNull Long> permissionIds) {
}
