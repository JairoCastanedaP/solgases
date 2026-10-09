package com.solgases.infrastructure.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Access token to send as 'Authorization: Bearer <token>'. There is no refresh token.")
public record AccessTokenResponse(

        @Schema(description = "Signed JWT (HS256). It identifies the user only; permissions are checked on every request")
        String accessToken,

        @Schema(description = "Token type", example = "Bearer")
        String tokenType,

        @Schema(description = "Lifetime of the token in seconds", example = "900")
        long expiresIn) {

    @Override
    public String toString() {
        return "AccessTokenResponse[accessToken=****, tokenType=" + tokenType + ", expiresIn=" + expiresIn + "]";
    }
}
