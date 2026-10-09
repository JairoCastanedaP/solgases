package com.solgases.infrastructure;

import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.test.context.DynamicPropertyRegistry;

/** Random HS256 key generated for each test run, so that no signing key is ever stored in the repository. */
public final class TestJwtSecret {

    public static final String VALUE = generate();

    private TestJwtSecret() {
    }

    public static void register(DynamicPropertyRegistry registry) {
        registry.add("solgases.security.jwt.secret", () -> VALUE);
    }

    public static String generate() {
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }
}
