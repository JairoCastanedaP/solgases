package com.solgases.application.exception;

import java.util.Collection;

public class PermissionNotFoundException extends ResourceNotFoundException {

    public PermissionNotFoundException(Long id) {
        super("Permission not found with id " + id);
    }

    public PermissionNotFoundException(Collection<Long> ids) {
        super("Permissions not found with ids " + ids);
    }
}
