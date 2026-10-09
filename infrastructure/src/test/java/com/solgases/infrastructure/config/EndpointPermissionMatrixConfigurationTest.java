package com.solgases.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.solgases.infrastructure.security.PermissionMatrixAuthorizationManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

/** Checks every approved endpoint–permission pair of the real matrix, and deny by default. */
class EndpointPermissionMatrixConfigurationTest {

    private final PermissionMatrixAuthorizationManager manager = new PermissionMatrixAuthorizationManager(
            new EndpointPermissionMatrixConfiguration().endpointPermissionMatrix());

    static Stream<Arguments> approvedMatrix() {
        List<Arguments> rows = new ArrayList<>();
        for (String resource : List.of("/api/categories", "/api/units-of-measure", "/api/products")) {
            rows.addAll(activatable(resource, "CATALOG_READ", "CATALOG_WRITE"));
        }
        rows.addAll(activatable("/api/users", "USER_READ", "USER_WRITE"));
        for (String resource : List.of("/api/roles", "/api/permissions")) {
            rows.addAll(crud(resource, "ACCESS_READ", "ACCESS_WRITE"));
        }
        String inventory = "/api/products/3/inventory";
        rows.add(Arguments.of("GET", inventory, "INVENTORY_READ"));
        rows.add(Arguments.of("GET", inventory + "/movements", "INVENTORY_READ"));
        rows.add(Arguments.of("GET", inventory + "/movements/8", "INVENTORY_READ"));
        rows.add(Arguments.of("POST", inventory + "/entries", "INVENTORY_MOVE"));
        rows.add(Arguments.of("POST", inventory + "/exits", "INVENTORY_MOVE"));
        rows.add(Arguments.of("POST", inventory + "/adjustments", "INVENTORY_ADJUST"));
        assertThat(rows).hasSize(38);
        return rows.stream();
    }

    private static List<Arguments> crud(String path, String readKey, String writeKey) {
        return new ArrayList<>(List.of(
                Arguments.of("GET", path, readKey),
                Arguments.of("GET", path + "/1", readKey),
                Arguments.of("POST", path, writeKey),
                Arguments.of("PUT", path + "/1", writeKey)));
    }

    private static List<Arguments> activatable(String path, String readKey, String writeKey) {
        List<Arguments> rows = crud(path, readKey, writeKey);
        rows.add(Arguments.of("PATCH", path + "/1/activate", writeKey));
        rows.add(Arguments.of("PATCH", path + "/1/deactivate", writeKey));
        return rows;
    }

    private boolean granted(String method, String path, String... authorities) {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("5", null, authorities);
        authentication.setAuthenticated(true);
        return granted(method, path, authentication);
    }

    private boolean granted(String method, String path, Authentication authentication) {
        return manager.authorize(() -> authentication,
                new RequestAuthorizationContext(new MockHttpServletRequest(method, path))).isGranted();
    }

    @ParameterizedTest(name = "{0} {1} requires {2}")
    @MethodSource("approvedMatrix")
    void eachOperationIsGrantedOnlyByItsPermission(String method, String path, String permissionKey) {
        assertThat(granted(method, path, permissionKey)).isTrue();

        String[] otherPermissions = InitialRolePermissionCatalog.BUSINESS_PERMISSIONS.stream()
                .filter(key -> !key.equals(permissionKey))
                .toArray(String[]::new);
        assertThat(granted(method, path, otherPermissions)).isFalse();
    }

    @ParameterizedTest(name = "{0} {1} is denied by default")
    @MethodSource("unmatchedRequests")
    void requestsOutsideTheMatrixAreDeniedEvenWithEveryPermission(String method, String path) {
        String[] everyPermission = InitialRolePermissionCatalog.BUSINESS_PERMISSIONS.toArray(String[]::new);

        assertThat(granted(method, path, everyPermission)).isFalse();
    }

    static Stream<Arguments> unmatchedRequests() {
        return Stream.of(
                Arguments.of("DELETE", "/api/users/1"),
                Arguments.of("DELETE", "/api/products/1"),
                Arguments.of("PATCH", "/api/roles/1/activate"),
                Arguments.of("GET", "/api/unknown"),
                Arguments.of("GET", "/api/categories/1/extra"),
                Arguments.of("PUT", "/api/products/3/inventory"),
                Arguments.of("GET", "/error"),
                // Documentation routes are outside the matrix: public by profile in SecurityConfiguration only
                Arguments.of("GET", "/v3/api-docs"),
                Arguments.of("GET", "/swagger-ui/index.html"));
    }

    @ParameterizedTest(name = "{0} {1} needs authentication")
    @MethodSource("approvedMatrix")
    void unauthenticatedRequestsAreDenied(String method, String path, String permissionKey) {
        TestingAuthenticationToken unauthenticated = new TestingAuthenticationToken("5", null, permissionKey);
        unauthenticated.setAuthenticated(false);

        assertThat(granted(method, path, unauthenticated)).isFalse();
    }
}
