package com.solgases.application.exception;

public class DuplicatePermissionKeyException extends ConflictException {

    public DuplicatePermissionKeyException(String key) {
        super("A permission with the key '" + key + "' already exists");
    }
}
