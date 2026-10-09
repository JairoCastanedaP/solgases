package com.solgases.infrastructure.security;

import java.util.List;
import java.util.function.Supplier;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

/**
 * Deny-by-default authorization: an authenticated user may access a request only when the first matching
 * rule of the matrix requires a permission key the user currently has. Unmatched requests are denied.
 */
@Component
public class PermissionMatrixAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private static final AuthorizationDecision DENIED = new AuthorizationDecision(false);
    private static final AuthorizationDecision GRANTED = new AuthorizationDecision(true);

    private final List<CompiledRule> rules;
    private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

    public PermissionMatrixAuthorizationManager(EndpointPermissionMatrix matrix) {
        this.rules = matrix.rules().stream()
                .map(rule -> new CompiledRule(
                        PathPatternRequestMatcher.withDefaults().matcher(rule.method(), rule.pathPattern()),
                        rule.permissionKey()))
                .toList();
    }

    @Override
    public AuthorizationResult authorize(Supplier<? extends Authentication> authentication,
            RequestAuthorizationContext context) {
        Authentication current = authentication.get();
        if (current == null || !current.isAuthenticated() || trustResolver.isAnonymous(current)) {
            return DENIED;
        }
        return rules.stream()
                .filter(rule -> rule.matcher().matches(context.getRequest()))
                .findFirst()
                .map(rule -> hasAuthority(current, rule.permissionKey()) ? GRANTED : DENIED)
                .orElse(DENIED);
    }

    private static boolean hasAuthority(Authentication authentication, String permissionKey) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(permissionKey::equals);
    }

    private record CompiledRule(RequestMatcher matcher, String permissionKey) {
    }
}
