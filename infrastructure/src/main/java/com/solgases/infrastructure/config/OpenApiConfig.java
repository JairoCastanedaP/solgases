package com.solgases.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String INTERNAL_SERVER_ERROR = "500";
    private static final String PROBLEM_JSON = "application/problem+json";
    private static final String PROBLEM_DETAIL_SCHEMA = "#/components/schemas/ProblemDetail";

    @Bean
    public OpenAPI solgasesOpenApi() {
        return new OpenAPI().info(new Info()
                .title("SOLGASES API")
                .description("Backend API for catalog, inventory and user/role/permission administration")
                .version("0.0.1"));
    }

    /**
     * Documents the generic 500 response of GlobalExceptionHandler on every operation, since any of them can
     * fail unexpectedly. It reuses the ProblemDetail schema already generated for the 4xx responses.
     */
    @Bean
    public OpenApiCustomizer internalServerErrorResponseCustomizer() {
        return openApi -> {
            if (openApi.getPaths() == null) {
                return;
            }
            openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation ->
                    operation.getResponses().putIfAbsent(INTERNAL_SERVER_ERROR, internalServerErrorResponse())));
        };
    }

    private static ApiResponse internalServerErrorResponse() {
        return new ApiResponse()
                .description("Unexpected error. The response does not include internal details")
                .content(new Content().addMediaType(PROBLEM_JSON,
                        new MediaType().schema(new Schema<>().$ref(PROBLEM_DETAIL_SCHEMA))));
    }
}
