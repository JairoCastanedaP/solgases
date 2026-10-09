package com.solgases.application.port.out;

public interface PasswordHashVerifier {

    /** Whether the raw password matches the stored hash. */
    boolean matches(String rawPassword, String passwordHash);

    /**
     * Performs a verification of comparable cost without a stored hash, so that an unknown username takes
     * about as long to reject as a wrong password.
     */
    void verifyAgainstDummyHash(String rawPassword);
}
