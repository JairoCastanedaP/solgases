package com.solgases.application.usecase;

import com.solgases.application.dto.RoleResult;
import com.solgases.application.dto.RoleUpdateCommand;
import com.solgases.application.exception.DuplicateRoleNameException;
import com.solgases.application.exception.RoleNotFoundException;
import com.solgases.application.port.in.UpdateRoleUseCase;
import com.solgases.application.port.out.PermissionPersistencePort;
import com.solgases.application.port.out.RolePersistencePort;
import org.springframework.transaction.annotation.Transactional;

public class UpdateRoleService implements UpdateRoleUseCase {

    private final RolePersistencePort rolePersistencePort;
    private final PermissionPersistencePort permissionPersistencePort;

    public UpdateRoleService(RolePersistencePort rolePersistencePort,
            PermissionPersistencePort permissionPersistencePort) {
        this.rolePersistencePort = rolePersistencePort;
        this.permissionPersistencePort = permissionPersistencePort;
    }

    /** Replaces name and permissions. The internal key is never changed. */
    @Override
    @Transactional
    public RoleResult execute(Long id, RoleUpdateCommand command) {
        var role = rolePersistencePort.findById(id).orElseThrow(() -> new RoleNotFoundException(id));
        if (rolePersistencePort.existsByNameAndIdNot(command.name(), id)) {
            throw new DuplicateRoleNameException(command.name());
        }
        var permissions = AccessReferenceResolver.resolvePermissions(permissionPersistencePort,
                command.permissionIds());
        return RoleResult.from(rolePersistencePort.saveChanges(role.withDetails(command.name(), permissions)));
    }
}
