package com.solgases.application.exception;

import java.util.Collection;

public class RoleNotFoundException extends ResourceNotFoundException {

    public RoleNotFoundException(Long id) {
        super("Role not found with id " + id);
    }

    public RoleNotFoundException(Collection<Long> ids) {
        super("Roles not found with ids " + ids);
    }
}
