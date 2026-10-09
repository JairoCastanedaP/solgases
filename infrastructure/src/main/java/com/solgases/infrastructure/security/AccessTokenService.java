package com.solgases.infrastructure.security;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

/**
 * Issues HS256 access tokens valid for 15 minutes. The token only identifies the user (subject); roles and
 * permissions are not included, because they are resolved from persistence on every protected request.
 */
@Component
public class AccessTokenService {

    public static final Duration ACCESS_TOKEN_TTL = Duration.ofMinutes(15);

    private final JwtEncoder jwtEncoder;
    private final Clock clock;

    public AccessTokenService(JwtEncoder jwtEncoder, Clock clock) {
        this.jwtEncoder = jwtEncoder;
        this.clock = clock;
    }

    public IssuedAccessToken issue(Long userId) {
        Instant issuedAt = clock.instant();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(String.valueOf(userId))
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plus(ACCESS_TOKEN_TTL))
                .build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();
        return new IssuedAccessToken(token, ACCESS_TOKEN_TTL.toSeconds());
    }

    /** An encoded access token and its lifetime in seconds. The token is never included in toString(). */
    public record IssuedAccessToken(String value, long expiresInSeconds) {

        @Override
        public String toString() {
            return "IssuedAccessToken[value=****, expiresInSeconds=" + expiresInSeconds + "]";
        }
    }
}
