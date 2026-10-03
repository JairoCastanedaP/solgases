package com.solgases.application.usecase;

import com.solgases.application.dto.PermissionCreateCommand;
import com.solgases.application.dto.PermissionResult;
import com.solgases.application.exception.DuplicatePermissionCodeException;
import com.solgases.application.exception.DuplicatePermissionKeyException;
import com.solgases.application.port.in.CreatePermissionUseCase;
import com.solgases.application.port.out.PermissionPersistencePort;
import com.solgases.domain.model.Permission;
import org.springframework.transaction.annotation.Transactional;

public class CreatePermissionService implements CreatePermissionUseCase {

    private final PermissionPersistencePort permissionPersistencePort;

    public CreatePermissionService(PermissionPersistencePort permissionPersistencePort) {
        this.permissionPersistencePort = permissionPersistencePort;
    }

    @Override
    @Transactional
    public PermissionResult execute(PermissionCreateCommand command) {
        if (permissionPersistencePort.existsByKey(command.key())) {
            throw new DuplicatePermissionKeyException(command.key());
        }
        if (permissionPersistencePort.existsByCode(command.code())) {
            throw new DuplicatePermissionCodeException(command.code());
        }
        return PermissionResult.from(permissionPersistencePort.saveNew(
                Permission.newPermission(command.key(), command.code())));
    }
}
