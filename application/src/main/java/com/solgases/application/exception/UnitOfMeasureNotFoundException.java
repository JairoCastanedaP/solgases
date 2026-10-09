package com.solgases.application.exception;

public class UnitOfMeasureNotFoundException extends RuntimeException {

    public UnitOfMeasureNotFoundException(Long id) {
        super("Unit of measure not found with id " + id);
    }
}
