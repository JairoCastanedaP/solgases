package com.solgases.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.solgases.infrastructure.TestJwtSecret;
import java.util.Base64;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

/** Startup validation of the signing key and of the Argon2id cost, plus a measurement of that cost. */
class SecurityConfigurationUnitTest {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfigurationUnitTest.class);
    private static final SecurityProperties.Argon2 MINIMUM_ARGON2 = new SecurityProperties.Argon2(19456, 2, 1);

    private static SecurityProperties withSecret(String secret) {
        return new SecurityProperties(new SecurityProperties.Jwt(secret), MINIMUM_ARGON2);
    }

    @Test
    void missingMalformedOrShortSigningKeysAreRejectedWithoutEchoingThem() {
        String shortKey = Base64.getEncoder().encodeToString("too-short-key".getBytes());
        String malformed = "not base64 !" + UUID.randomUUID();

        assertThatThrownBy(() -> JwtConfiguration.signingKey(withSecret(null)))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("not configured");
        assertThatThrownBy(() -> JwtConfiguration.signingKey(withSecret(" ")))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("not configured");
        assertThatThrownBy(() -> JwtConfiguration.signingKey(withSecret(malformed)))
                .isInstanceOf(IllegalStateException.class).hasMessageNotContaining(malformed);
        assertThatThrownBy(() -> JwtConfiguration.signingKey(withSecret(shortKey)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("256 bits")
                .hasMessageNotContaining(shortKey);
    }

    @Test
    void validSigningKeyIsAcceptedAndNeverPrinted() {
        String secret = TestJwtSecret.generate();

        assertThat(JwtConfiguration.signingKey(withSecret(secret)).getAlgorithm()).isEqualTo("HmacSHA256");
        assertThat(withSecret(secret).toString()).doesNotContain(secret);
    }

    @Test
    void argon2ParametersBelowTheOwaspMinimumAreRejected() {
        assertThatThrownBy(() -> new SecurityProperties.Argon2(19455, 2, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new SecurityProperties.Argon2(19456, 1, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new SecurityProperties.Argon2(19456, 2, 0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void argon2idHashesUseTheConfiguredCostAndVerifyOnlyTheRightPassword() {
        Argon2PasswordEncoder encoder = new PasswordHashingConfiguration()
                .argon2PasswordEncoder(withSecret(TestJwtSecret.generate()));
        Argon2PasswordHashVerifier verifier = new Argon2PasswordHashVerifier(encoder);
        String password = "pw-" + UUID.randomUUID();

        long start = System.nanoTime();
        int rounds = 5;
        String hash = null;
        for (int i = 0; i < rounds; i++) {
            hash = encoder.encode(password);
        }
        long averageMillis = (System.nanoTime() - start) / rounds / 1_000_000;
        log.info("Argon2id (m=19456 KiB, t=2, p=1) average hashing time on this machine: {} ms", averageMillis);

        assertThat(hash).startsWith("$argon2id$v=19$m=19456,t=2,p=1$").doesNotContain(password);
        assertThat(verifier.matches(password, hash)).isTrue();
        assertThat(verifier.matches(password + "x", hash)).isFalse();
        verifier.verifyAgainstDummyHash(password);
    }
}
