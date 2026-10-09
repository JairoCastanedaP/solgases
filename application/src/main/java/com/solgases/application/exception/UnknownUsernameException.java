package com.solgases.application.exception;

public class UnknownUsernameException extends ResourceNotFoundException {

    public UnknownUsernameException() {
        super("No user exists with the given username");
    }
}
