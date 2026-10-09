package com.solgases.infrastructure.api.dto;

import com.solgases.domain.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Schema(description = "Credentials of an internal user. The password is never logged or returned.")
public record AccessTokenRequest(

        @Schema(description = "Username of the user", example = "jdoe", requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = User.USERNAME_MAX_LENGTH)
        @NotBlank
        @Size(max = User.USERNAME_MAX_LENGTH)
        String username,

        @Schema(description = "Password of the user", example = "<password>", format = "password",
                accessMode = Schema.AccessMode.WRITE_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
        // Only emptiness is a client error: spaces are valid password characters, and lengths above the policy
        // maximum get the generic 401 of the authentication use case
        @NotEmpty
        String password) {

    @Override
    public String toString() {
        return "AccessTokenRequest[username=" + username + ", password=****]";
    }
}
