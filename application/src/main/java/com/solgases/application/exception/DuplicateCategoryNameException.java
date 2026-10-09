package com.solgases.application.exception;

public class DuplicateCategoryNameException extends RuntimeException {

    public DuplicateCategoryNameException(String name) {
        super("A category with the name '" + name + "' already exists");
    }
}
