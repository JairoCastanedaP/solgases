package com.solgases.infrastructure.api.dto;

import com.solgases.domain.model.User;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

@Schema(description = "Editable data of a user. The id and the active flag cannot be set by the client; "
        + "a new user is always active. No password or credential is accepted.")
public record UserRequest(

        @Schema(description = "Unique username. Stored exactly as received (no trimming or normalization).",
                example = "jcastaneda", requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = User.USERNAME_MAX_LENGTH)
        @NotBlank
        @Size(max = User.USERNAME_MAX_LENGTH)
        String username,

        @Schema(description = "Name shown for the user", example = "Jairo Castañeda",
                requiredMode = Schema.RequiredMode.REQUIRED, maxLength = User.DISPLAY_NAME_MAX_LENGTH)
        @NotBlank
        @Size(max = User.DISPLAY_NAME_MAX_LENGTH)
        String displayName,

        @ArraySchema(arraySchema = @Schema(description = "Ids of existing roles. Replaces the current roles; "
                + "an empty list removes them all.", requiredMode = Schema.RequiredMode.REQUIRED),
                schema = @Schema(example = "1"))
        @NotNull
        Set<@NotNull Long> roleIds) {
}
