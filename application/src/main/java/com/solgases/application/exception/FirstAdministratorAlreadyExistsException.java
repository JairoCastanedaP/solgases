package com.solgases.application.exception;

public class FirstAdministratorAlreadyExistsException extends ConflictException {

    public FirstAdministratorAlreadyExistsException() {
        super("An active administrator already exists; the first administrator cannot be created again");
    }
}
