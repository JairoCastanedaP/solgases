package com.solgases.infrastructure.config;

import static com.solgases.infrastructure.config.InitialRolePermissionCatalog.ACCESS_READ;
import static com.solgases.infrastructure.config.InitialRolePermissionCatalog.ACCESS_WRITE;
import static com.solgases.infrastructure.config.InitialRolePermissionCatalog.CATALOG_READ;
import static com.solgases.infrastructure.config.InitialRolePermissionCatalog.CATALOG_WRITE;
import static com.solgases.infrastructure.config.InitialRolePermissionCatalog.INVENTORY_ADJUST;
import static com.solgases.infrastructure.config.InitialRolePermissionCatalog.INVENTORY_MOVE;
import static com.solgases.infrastructure.config.InitialRolePermissionCatalog.INVENTORY_READ;
import static com.solgases.infrastructure.config.InitialRolePermissionCatalog.USER_READ;
import static com.solgases.infrastructure.config.InitialRolePermissionCatalog.USER_WRITE;

import com.solgases.infrastructure.security.EndpointPermissionMatrix;
import com.solgases.infrastructure.security.EndpointPermissionRule;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

/**
 * Endpoint–permission matrix approved for Increment 7. Every pattern is exact (no wildcards), so the inventory
 * routes under /api/products/{productId}/inventory never match a product rule. Requests not covered here are
 * denied. The token operation and the profile-dependent Swagger/OpenAPI routes are configured in
 * SecurityConfiguration, outside this matrix.
 */
@Configuration
public class EndpointPermissionMatrixConfiguration {

    private static final String INVENTORY = "/api/products/{productId}/inventory";

    @Bean
    EndpointPermissionMatrix endpointPermissionMatrix() {
        List<EndpointPermissionRule> rules = new ArrayList<>();
        rules.add(rule(HttpMethod.GET, INVENTORY, INVENTORY_READ));
        rules.add(rule(HttpMethod.GET, INVENTORY + "/movements", INVENTORY_READ));
        rules.add(rule(HttpMethod.GET, INVENTORY + "/movements/{movementId}", INVENTORY_READ));
        rules.add(rule(HttpMethod.POST, INVENTORY + "/entries", INVENTORY_MOVE));
        rules.add(rule(HttpMethod.POST, INVENTORY + "/exits", INVENTORY_MOVE));
        rules.add(rule(HttpMethod.POST, INVENTORY + "/adjustments", INVENTORY_ADJUST));
        for (String resource : List.of("/api/categories", "/api/units-of-measure", "/api/products")) {
            rules.addAll(activatableResource(resource, CATALOG_READ, CATALOG_WRITE));
        }
        rules.addAll(activatableResource("/api/users", USER_READ, USER_WRITE));
        for (String resource : List.of("/api/roles", "/api/permissions")) {
            rules.addAll(resource(resource, ACCESS_READ, ACCESS_WRITE));
        }
        return new EndpointPermissionMatrix(rules);
    }

    // List, read, create and update
    private static List<EndpointPermissionRule> resource(String path, String readKey, String writeKey) {
        return List.of(
                rule(HttpMethod.GET, path, readKey),
                rule(HttpMethod.GET, path + "/{id}", readKey),
                rule(HttpMethod.POST, path, writeKey),
                rule(HttpMethod.PUT, path + "/{id}", writeKey));
    }

    // The same operations plus activation and deactivation
    private static List<EndpointPermissionRule> activatableResource(String path, String readKey, String writeKey) {
        List<EndpointPermissionRule> rules = new ArrayList<>(resource(path, readKey, writeKey));
        rules.add(rule(HttpMethod.PATCH, path + "/{id}/activate", writeKey));
        rules.add(rule(HttpMethod.PATCH, path + "/{id}/deactivate", writeKey));
        return rules;
    }

    private static EndpointPermissionRule rule(HttpMethod method, String pathPattern, String permissionKey) {
        return new EndpointPermissionRule(method, pathPattern, permissionKey);
    }
}
