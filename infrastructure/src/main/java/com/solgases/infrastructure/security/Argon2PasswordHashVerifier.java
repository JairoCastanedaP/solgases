package com.solgases.infrastructure.security;

import com.solgases.application.port.out.PasswordHashVerifier;
import java.util.UUID;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Argon2PasswordHashVerifier implements PasswordHashVerifier {

    private final Argon2PasswordEncoder encoder;
    // Hash of a random value, computed once, used to spend comparable time when the username is unknown
    private final String dummyHash;

    public Argon2PasswordHashVerifier(Argon2PasswordEncoder encoder) {
        this.encoder = encoder;
        this.dummyHash = encoder.encode(UUID.randomUUID().toString());
    }

    @Override
    public boolean matches(String rawPassword, String passwordHash) {
        return encoder.matches(rawPassword, passwordHash);
    }

    @Override
    public void verifyAgainstDummyHash(String rawPassword) {
        encoder.matches(rawPassword, dummyHash);
    }
}
