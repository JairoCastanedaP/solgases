package com.solgases.application.usecase;

import com.solgases.application.dto.RoleCreateCommand;
import com.solgases.application.dto.RoleResult;
import com.solgases.application.exception.DuplicateRoleKeyException;
import com.solgases.application.exception.DuplicateRoleNameException;
import com.solgases.application.port.in.CreateRoleUseCase;
import com.solgases.application.port.out.PermissionPersistencePort;
import com.solgases.application.port.out.RolePersistencePort;
import com.solgases.domain.model.Role;
import org.springframework.transaction.annotation.Transactional;

public class CreateRoleService implements CreateRoleUseCase {

    private final RolePersistencePort rolePersistencePort;
    private final PermissionPersistencePort permissionPersistencePort;

    public CreateRoleService(RolePersistencePort rolePersistencePort,
            PermissionPersistencePort permissionPersistencePort) {
        this.rolePersistencePort = rolePersistencePort;
        this.permissionPersistencePort = permissionPersistencePort;
    }

    @Override
    @Transactional
    public RoleResult execute(RoleCreateCommand command) {
        if (rolePersistencePort.existsByKey(command.key())) {
            throw new DuplicateRoleKeyException(command.key());
        }
        if (rolePersistencePort.existsByName(command.name())) {
            throw new DuplicateRoleNameException(command.name());
        }
        var permissions = AccessReferenceResolver.resolvePermissions(permissionPersistencePort,
                command.permissionIds());
        return RoleResult.from(rolePersistencePort.saveNew(Role.newRole(command.key(), command.name(), permissions)));
    }
}
