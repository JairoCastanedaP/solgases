package com.solgases.application.exception;

public class DuplicateUnitOfMeasureCodeException extends RuntimeException {

    public DuplicateUnitOfMeasureCodeException(String code) {
        super("A unit of measure with the code '" + code + "' already exists");
    }
}
