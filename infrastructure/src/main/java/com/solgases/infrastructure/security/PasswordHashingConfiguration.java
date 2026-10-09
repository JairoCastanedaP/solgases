package com.solgases.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

@Configuration
public class PasswordHashingConfiguration {

    private static final int SALT_LENGTH_BYTES = 16;
    private static final int HASH_LENGTH_BYTES = 32;

    /** Argon2id encoder; the version and cost parameters are stored inside every encoded hash. */
    @Bean
    public Argon2PasswordEncoder argon2PasswordEncoder(SecurityProperties properties) {
        SecurityProperties.Argon2 argon2 = properties.argon2();
        return new Argon2PasswordEncoder(SALT_LENGTH_BYTES, HASH_LENGTH_BYTES, argon2.parallelism(),
                argon2.memoryKib(), argon2.iterations());
    }
}
