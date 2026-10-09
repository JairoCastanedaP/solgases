package com.solgases.infrastructure.security;

import java.time.Clock;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

/** HS256 signing and verification of access tokens with a key provided outside the repository. */
@Configuration
public class JwtConfiguration {

    static final int MIN_KEY_LENGTH_BYTES = 32;

    @Bean
    public JwtEncoder jwtEncoder(SecurityProperties properties) {
        return NimbusJwtEncoder.withSecretKey(signingKey(properties)).algorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    public JwtDecoder jwtDecoder(SecurityProperties properties) {
        return NimbusJwtDecoder.withSecretKey(signingKey(properties)).macAlgorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    /** Decodes the configured key, failing without revealing it when it is missing, malformed or too short. */
    static SecretKey signingKey(SecurityProperties properties) {
        String secret = properties.jwt().secret();
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("The JWT signing key is not configured (solgases.security.jwt.secret)");
        }
        byte[] key;
        try {
            key = Base64.getDecoder().decode(secret.trim());
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("The JWT signing key must be Base64-encoded");
        }
        if (key.length < MIN_KEY_LENGTH_BYTES) {
            throw new IllegalStateException(
                    "The JWT signing key must have at least " + MIN_KEY_LENGTH_BYTES * 8 + " bits for HS256");
        }
        return new SecretKeySpec(key, "HmacSHA256");
    }
}
