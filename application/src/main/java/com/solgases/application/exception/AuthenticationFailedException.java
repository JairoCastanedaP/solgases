package com.solgases.application.exception;

/**
 * Authentication was rejected. The message is deliberately generic, so it does not reveal whether the
 * username exists, the password is wrong or the user is inactive.
 */
public class AuthenticationFailedException extends RuntimeException {

    public AuthenticationFailedException() {
        super("Invalid username or password");
    }
}
