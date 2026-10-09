package com.solgases.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

/** The permission keys used here (TEST_*) exist only in this test; the real matrix is still pending. */
class PermissionMatrixAuthorizationManagerTest {

    private final PermissionMatrixAuthorizationManager manager = new PermissionMatrixAuthorizationManager(
            new EndpointPermissionMatrix(List.of(
                    new EndpointPermissionRule(HttpMethod.GET, "/api/categories/{id}", "TEST_READ"),
                    new EndpointPermissionRule(HttpMethod.PUT, "/api/categories/{id}", "TEST_WRITE"))));

    private static RequestAuthorizationContext request(String method, String path) {
        return new RequestAuthorizationContext(new MockHttpServletRequest(method, path));
    }

    private static Authentication user(String... authorities) {
        TestingAuthenticationToken token = new TestingAuthenticationToken("5", null, authorities);
        token.setAuthenticated(true);
        return token;
    }

    private boolean granted(Authentication authentication, RequestAuthorizationContext context) {
        return manager.authorize(() -> authentication, context).isGranted();
    }

    @Test
    void matchingRuleGrantsOnlyUsersWithItsPermissionKey() {
        assertThat(granted(user("TEST_READ"), request("GET", "/api/categories/1"))).isTrue();
        assertThat(granted(user("TEST_WRITE"), request("GET", "/api/categories/1"))).isFalse();
        assertThat(granted(user("TEST_WRITE"), request("PUT", "/api/categories/1"))).isTrue();
    }

    @Test
    void requestsWithoutARuleAreDeniedByDefault() {
        assertThat(granted(user("TEST_READ", "TEST_WRITE"), request("DELETE", "/api/categories/1"))).isFalse();
        assertThat(granted(user("TEST_READ"), request("GET", "/api/products"))).isFalse();
    }

    @Test
    void anonymousMissingOrUnauthenticatedUsersAreDenied() {
        Authentication anonymous = new AnonymousAuthenticationToken("key", "anonymous",
                AuthorityUtils.createAuthorityList("TEST_READ"));
        TestingAuthenticationToken notAuthenticated = new TestingAuthenticationToken("5", null, "TEST_READ");
        notAuthenticated.setAuthenticated(false);

        assertThat(granted(anonymous, request("GET", "/api/categories/1"))).isFalse();
        assertThat(granted(null, request("GET", "/api/categories/1"))).isFalse();
        assertThat(granted(notAuthenticated, request("GET", "/api/categories/1"))).isFalse();
    }

    @Test
    void emptyMatrixDeniesEverything() {
        PermissionMatrixAuthorizationManager empty =
                new PermissionMatrixAuthorizationManager(new EndpointPermissionMatrix(List.of()));

        assertThat(empty.authorize(() -> user("TEST_READ"), request("GET", "/api/categories/1")).isGranted())
                .isFalse();
    }
}
