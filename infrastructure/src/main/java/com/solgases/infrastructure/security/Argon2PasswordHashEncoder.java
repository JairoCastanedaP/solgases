package com.solgases.infrastructure.security;

import com.solgases.application.port.out.PasswordHashEncoder;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Component;

/** Encodes new passwords with the same Argon2id encoder used to verify them. */
@Component
public class Argon2PasswordHashEncoder implements PasswordHashEncoder {

    private final Argon2PasswordEncoder encoder;

    public Argon2PasswordHashEncoder(Argon2PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return encoder.encode(rawPassword);
    }
}
