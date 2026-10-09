package com.solgases.application.exception;

public class DuplicateUsernameException extends ConflictException {

    public DuplicateUsernameException(String username) {
        super("A user with the username '" + username + "' already exists");
    }
}
