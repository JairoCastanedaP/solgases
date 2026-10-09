package com.solgases.application.exception;

public class CredentialAlreadyExistsException extends ConflictException {

    public CredentialAlreadyExistsException(Long userId) {
        super("User " + userId + " already has a credential; it is not overwritten. "
                + "Changing it requires a password change flow, which is not implemented yet");
    }
}
