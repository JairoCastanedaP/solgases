package com.solgases.application.exception;

public class DuplicatePermissionCodeException extends ConflictException {

    public DuplicatePermissionCodeException(String code) {
        super("A permission with the code '" + code + "' already exists");
    }
}
