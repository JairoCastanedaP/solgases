package com.solgases.application.exception;

public class DuplicateRoleKeyException extends ConflictException {

    public DuplicateRoleKeyException(String key) {
        super("A role with the key '" + key + "' already exists");
    }
}
