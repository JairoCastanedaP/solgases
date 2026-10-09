package com.solgases.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.solgases.application.dto.UserAccess;
import com.solgases.application.port.in.GetUserAccessUseCase;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

class SecurityComponentsTest {

    private static final JsonMapper JSON = JsonMapper.builder().build();

    private static Jwt jwt(String subject) {
        return Jwt.withTokenValue("token-value").header("alg", "HS256").subject(subject)
                .issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(900)).build();
    }

    @Test
    void converterUsesTheCurrentPermissionKeysOfAnActiveUser() {
        GetUserAccessUseCase access = mock(GetUserAccessUseCase.class);
        when(access.execute(5L)).thenReturn(Optional.of(new UserAccess(5L, true, Set.of("TEST_B", "TEST_A"))));

        AbstractAuthenticationToken authentication = new CurrentUserAuthenticationConverter(access).convert(jwt("5"));

        assertThat(authentication.getName()).isEqualTo("5");
        assertThat(authentication.getAuthorities()).extracting(GrantedAuthority::getAuthority)
                .containsExactly("TEST_A", "TEST_B");
    }

    @Test
    void converterRejectsInactiveMissingAndMalformedSubjects() {
        GetUserAccessUseCase access = mock(GetUserAccessUseCase.class);
        when(access.execute(5L)).thenReturn(Optional.of(new UserAccess(5L, false, Set.of("TEST_A"))));
        when(access.execute(6L)).thenReturn(Optional.empty());
        CurrentUserAuthenticationConverter converter = new CurrentUserAuthenticationConverter(access);

        for (String subject : new String[] {"5", "6", "not-a-number"}) {
            assertThatThrownBy(() -> converter.convert(jwt(subject)))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessage("The access token is not valid");
        }
    }

    @Test
    void errorHandlerWritesGeneric401And403ProblemDetails() throws Exception {
        ProblemDetailSecurityErrorHandler handler = new ProblemDetailSecurityErrorHandler(JSON);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/categories");
        MockHttpServletResponse unauthorized = new MockHttpServletResponse();
        MockHttpServletResponse forbidden = new MockHttpServletResponse();

        handler.commence(request, unauthorized, new BadCredentialsException("internal cause token-value"));
        handler.handle(request, forbidden, new AccessDeniedException("internal cause"));

        JsonNode problem401 = JSON.readTree(unauthorized.getContentAsString());
        assertThat(unauthorized.getStatus()).isEqualTo(401);
        assertThat(unauthorized.getContentType()).startsWith("application/problem+json");
        assertThat(unauthorized.getHeader("WWW-Authenticate")).isEqualTo("Bearer");
        assertThat(problem401.get("status").asInt()).isEqualTo(401);
        assertThat(problem401.get("instance").asString()).isEqualTo("/api/categories");
        assertThat(unauthorized.getContentAsString()).doesNotContain("internal cause").doesNotContain("token-value");

        JsonNode problem403 = JSON.readTree(forbidden.getContentAsString());
        assertThat(forbidden.getStatus()).isEqualTo(403);
        assertThat(problem403.get("title").asString()).isEqualTo("Forbidden");
        assertThat(forbidden.getContentAsString()).doesNotContain("internal cause");
    }

    @Test
    void issuedTokensAreNeverPrinted() {
        AccessTokenService.IssuedAccessToken token = new AccessTokenService.IssuedAccessToken("secret-token", 900);

        assertThat(token.toString()).doesNotContain("secret-token");
    }
}
