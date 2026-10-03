package com.solgases.application.exception;

public class DuplicateRoleNameException extends ConflictException {

    public DuplicateRoleNameException(String name) {
        super("A role with the name '" + name + "' already exists");
    }
}
