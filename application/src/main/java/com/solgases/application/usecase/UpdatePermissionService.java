package com.solgases.application.usecase;

import com.solgases.application.dto.PermissionResult;
import com.solgases.application.dto.PermissionUpdateCommand;
import com.solgases.application.exception.DuplicatePermissionCodeException;
import com.solgases.application.exception.PermissionNotFoundException;
import com.solgases.application.port.in.UpdatePermissionUseCase;
import com.solgases.application.port.out.PermissionPersistencePort;
import org.springframework.transaction.annotation.Transactional;

public class UpdatePermissionService implements UpdatePermissionUseCase {

    private final PermissionPersistencePort permissionPersistencePort;

    public UpdatePermissionService(PermissionPersistencePort permissionPersistencePort) {
        this.permissionPersistencePort = permissionPersistencePort;
    }

    /** Replaces the code. The internal key is never changed. */
    @Override
    @Transactional
    public PermissionResult execute(Long id, PermissionUpdateCommand command) {
        var permission = permissionPersistencePort.findById(id)
                .orElseThrow(() -> new PermissionNotFoundException(id));
        if (permissionPersistencePort.existsByCodeAndIdNot(command.code(), id)) {
            throw new DuplicatePermissionCodeException(command.code());
        }
        return PermissionResult.from(permissionPersistencePort.saveChanges(permission.withCode(command.code())));
    }
}
