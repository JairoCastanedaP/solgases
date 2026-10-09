package com.solgases.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Security settings. The JWT signing key comes from configuration outside the repository (JWT_SECRET) and is
 * never printed. Argon2id parameters cannot go below the OWASP minimum recorded in docs/mvp1.md.
 */
@ConfigurationProperties("solgases.security")
public record SecurityProperties(@DefaultValue Jwt jwt, @DefaultValue Argon2 argon2) {

    /** Base64-encoded HS256 signing key. */
    public record Jwt(String secret) {

        @Override
        public String toString() {
            return "Jwt[secret=****]";
        }
    }

    /** Argon2id cost parameters; memory is expressed in KiB. */
    public record Argon2(@DefaultValue("19456") int memoryKib, @DefaultValue("2") int iterations,
            @DefaultValue("1") int parallelism) {

        public static final int MIN_MEMORY_KIB = 19 * 1024;
        public static final int MIN_ITERATIONS = 2;
        public static final int MIN_PARALLELISM = 1;

        public Argon2 {
            if (memoryKib < MIN_MEMORY_KIB || iterations < MIN_ITERATIONS || parallelism < MIN_PARALLELISM) {
                throw new IllegalArgumentException("Argon2id parameters are below the minimum of "
                        + MIN_MEMORY_KIB + " KiB of memory, " + MIN_ITERATIONS + " iterations and parallelism "
                        + MIN_PARALLELISM);
            }
        }
    }
}
