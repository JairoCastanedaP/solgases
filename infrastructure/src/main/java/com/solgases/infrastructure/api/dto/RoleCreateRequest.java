package com.solgases.infrastructure.api.dto;

import com.solgases.domain.model.Role;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

@Schema(description = "Data to create a role. The key is set only here and can never change afterwards.")
public record RoleCreateRequest(

        @Schema(description = "Stable internal key. Unique and immutable once created.", example = "ROLE_KEY",
                requiredMode = Schema.RequiredMode.REQUIRED, maxLength = Role.KEY_MAX_LENGTH)
        @NotBlank
        @Size(max = Role.KEY_MAX_LENGTH)
        String key,

        @Schema(description = "Unique, editable role name", example = "Nombre del rol",
                requiredMode = Schema.RequiredMode.REQUIRED, maxLength = Role.NAME_MAX_LENGTH)
        @NotBlank
        @Size(max = Role.NAME_MAX_LENGTH)
        String name,

        @ArraySchema(arraySchema = @Schema(description = "Ids of existing permissions. May be empty: a role can "
                + "exist without permissions.", requiredMode = Schema.RequiredMode.REQUIRED),
                schema = @Schema(example = "1"))
        @NotNull
        Set<@NotNull Long> permissionIds) {
}
