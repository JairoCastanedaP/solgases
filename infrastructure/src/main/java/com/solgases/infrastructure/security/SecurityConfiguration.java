package com.solgases.infrastructure.security;

import com.solgases.infrastructure.api.rest.AuthenticationController;
import jakarta.servlet.DispatcherType;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Stateless JWT security with deny by default. Only the token operation and, where springdoc enables them, the
 * Swagger UI and OpenAPI routes are public (approved: public in local, dev and qa, disabled in prd); every other
 * request needs an authenticated user and a permission granted by the matrix.
 * CSRF does not apply (no cookies or sessions) and CORS stays disabled until client origins are approved.
 *
 * <p>The filter chain only exists in the servlet web application. Local credential commands run without a web
 * application, where Spring Security provides no {@code HttpSecurity}; the security properties they still need
 * (Argon2id, JWT) are registered by {@link SecurityPropertiesConfiguration}.
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfiguration {

    private static final String[] API_DOCS_PATHS = {"/v3/api-docs", "/v3/api-docs/**"};
    private static final String[] SWAGGER_UI_PATHS = {"/swagger-ui.html", "/swagger-ui/**"};

    private final boolean apiDocsEnabled;
    private final boolean swaggerUiEnabled;

    public SecurityConfiguration(@Value("${springdoc.api-docs.enabled:true}") boolean apiDocsEnabled,
            @Value("${springdoc.swagger-ui.enabled:true}") boolean swaggerUiEnabled) {
        this.apiDocsEnabled = apiDocsEnabled;
        this.swaggerUiEnabled = swaggerUiEnabled;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            PermissionMatrixAuthorizationManager permissionMatrixAuthorizationManager,
            CurrentUserAuthenticationConverter currentUserAuthenticationConverter,
            ProblemDetailSecurityErrorHandler errorHandler) {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .requestCache(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> {
                    // Internal error dispatches only; a direct request to /error is still denied
                    authorize.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll();
                    authorize.requestMatchers(HttpMethod.POST, AuthenticationController.TOKEN_PATH).permitAll();
                    String[] documentationPaths = publicDocumentationPaths();
                    if (documentationPaths.length > 0) {
                        authorize.requestMatchers(HttpMethod.GET, documentationPaths).permitAll();
                    }
                    authorize.anyRequest().access(permissionMatrixAuthorizationManager);
                })
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(currentUserAuthenticationConverter))
                        .authenticationEntryPoint(errorHandler)
                        .accessDeniedHandler(errorHandler))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(errorHandler)
                        .accessDeniedHandler(errorHandler))
                .build();
    }

    // Only the documentation routes that springdoc serves in the active profile are public
    String[] publicDocumentationPaths() {
        List<String> paths = new ArrayList<>();
        if (apiDocsEnabled) {
            paths.addAll(List.of(API_DOCS_PATHS));
        }
        if (swaggerUiEnabled) {
            paths.addAll(List.of(SWAGGER_UI_PATHS));
        }
        return paths.toArray(String[]::new);
    }
}
