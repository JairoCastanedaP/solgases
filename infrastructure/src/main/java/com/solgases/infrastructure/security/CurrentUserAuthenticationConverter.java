package com.solgases.infrastructure.security;

import com.solgases.application.dto.UserAccess;
import com.solgases.application.port.in.GetUserAccessUseCase;
import java.util.List;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Turns a valid access token into an authentication using the current state of the user: an inactive or
 * missing user is rejected, and the authorities are the current permission keys, not anything in the token.
 */
@Component
public class CurrentUserAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final GetUserAccessUseCase getUserAccessUseCase;

    public CurrentUserAuthenticationConverter(GetUserAccessUseCase getUserAccessUseCase) {
        this.getUserAccessUseCase = getUserAccessUseCase;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        UserAccess access = getUserAccessUseCase.execute(userId(jwt))
                .filter(UserAccess::active)
                .orElseThrow(() -> new BadCredentialsException("The access token is not valid"));
        List<SimpleGrantedAuthority> authorities = access.permissionKeys().stream()
                .sorted()
                .map(SimpleGrantedAuthority::new)
                .toList();
        return new JwtAuthenticationToken(jwt, authorities, String.valueOf(access.userId()));
    }

    private static Long userId(Jwt jwt) {
        try {
            return Long.valueOf(jwt.getSubject());
        } catch (NumberFormatException ex) {
            throw new BadCredentialsException("The access token is not valid");
        }
    }
}
