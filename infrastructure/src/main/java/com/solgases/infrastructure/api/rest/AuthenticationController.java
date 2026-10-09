package com.solgases.infrastructure.api.rest;

import com.solgases.application.dto.AuthenticatedUser;
import com.solgases.application.dto.AuthenticationCommand;
import com.solgases.application.port.in.AuthenticateUserUseCase;
import com.solgases.infrastructure.api.dto.AccessTokenRequest;
import com.solgases.infrastructure.api.dto.AccessTokenResponse;
import com.solgases.infrastructure.security.AccessTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** The only public operation of the API: exchanges valid credentials for a 15-minute access token. */
@RestController
@Tag(name = "Authentication", description = "Access tokens for internal users")
public class AuthenticationController {

    public static final String TOKEN_PATH = "/api/auth/token";
    private static final String PROBLEM_JSON = "application/problem+json";

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final AccessTokenService accessTokenService;

    public AuthenticationController(AuthenticateUserUseCase authenticateUserUseCase,
            AccessTokenService accessTokenService) {
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.accessTokenService = accessTokenService;
    }

    @PostMapping(TOKEN_PATH)
    @SecurityRequirements
    @Operation(summary = "Obtain an access token",
            description = "Authenticates an active internal user with username and password. Unknown users, "
                    + "wrong passwords and inactive users get the same 401 response.")
    @ApiResponse(responseCode = "200", description = "Access token issued",
            content = @Content(schema = @Schema(implementation = AccessTokenResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request body",
            content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "401", description = "Invalid username or password",
            content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    public AccessTokenResponse issueToken(@Valid @RequestBody AccessTokenRequest request) {
        AuthenticatedUser user = authenticateUserUseCase.execute(
                new AuthenticationCommand(request.username(), request.password()));
        AccessTokenService.IssuedAccessToken token = accessTokenService.issue(user.userId());
        return new AccessTokenResponse(token.value(), "Bearer", token.expiresInSeconds());
    }
}
