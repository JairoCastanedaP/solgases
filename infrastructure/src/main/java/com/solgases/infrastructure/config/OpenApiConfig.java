package com.solgases.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    static final String BEARER_AUTH = "bearerAuth";
    private static final String UNAUTHORIZED = "401";
    private static final String FORBIDDEN = "403";
    private static final String INTERNAL_SERVER_ERROR = "500";
    private static final String PROBLEM_JSON = "application/problem+json";
    private static final String PROBLEM_DETAIL_SCHEMA = "#/components/schemas/ProblemDetail";

    /** Every operation requires a bearer access token, except the ones that declare an empty security list. */
    @Bean
    public OpenAPI solgasesOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("SOLGASES API")
                        .description("Backend API for catalog, inventory and user/role/permission administration")
                        .version("0.0.1"))
                .components(new Components().addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Access token obtained from POST /api/auth/token; valid for 15 minutes")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
    }

    /**
     * Documents the generic 500 response of GlobalExceptionHandler on every operation, since any of them can
     * fail unexpectedly, and the 401/403 responses of the security filter chain on every protected operation.
     * It reuses the ProblemDetail schema already generated for the 4xx responses.
     */
    @Bean
    public OpenApiCustomizer errorResponsesCustomizer() {
        return openApi -> {
            if (openApi.getPaths() == null) {
                return;
            }
            openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
                if (isProtected(operation)) {
                    operation.getResponses().putIfAbsent(UNAUTHORIZED, problemResponse(
                            "Missing, invalid or expired access token, or inactive user"));
                    operation.getResponses().putIfAbsent(FORBIDDEN, problemResponse(
                            "The user does not have the permission required by this operation"));
                }
                operation.getResponses().putIfAbsent(INTERNAL_SERVER_ERROR, problemResponse(
                        "Unexpected error. The response does not include internal details"));
            }));
        };
    }

    // An operation with an explicit empty security list (such as the token operation) is public
    private static boolean isProtected(Operation operation) {
        return operation.getSecurity() == null || !operation.getSecurity().isEmpty();
    }

    private static ApiResponse problemResponse(String description) {
        return new ApiResponse()
                .description(description)
                .content(new Content().addMediaType(PROBLEM_JSON,
                        new MediaType().schema(new Schema<>().$ref(PROBLEM_DETAIL_SCHEMA))));
    }
}
