package com.solgases.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI solgasesOpenApi() {
        return new OpenAPI().info(new Info()
                .title("SOLGASES API")
                .description("Backend API for catalog and inventory management")
                .version("0.0.1"));
    }
}
