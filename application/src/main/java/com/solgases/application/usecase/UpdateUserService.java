package com.solgases.application.usecase;

import com.solgases.application.dto.UserCommand;
import com.solgases.application.dto.UserResult;
import com.solgases.application.exception.DuplicateUsernameException;
import com.solgases.application.exception.UserNotFoundException;
import com.solgases.application.port.in.UpdateUserUseCase;
import com.solgases.application.port.out.RolePersistencePort;
import com.solgases.application.port.out.UserPersistencePort;
import org.springframework.transaction.annotation.Transactional;

public class UpdateUserService implements UpdateUserUseCase {

    private final UserPersistencePort userPersistencePort;
    private final RolePersistencePort rolePersistencePort;

    public UpdateUserService(UserPersistencePort userPersistencePort, RolePersistencePort rolePersistencePort) {
        this.userPersistencePort = userPersistencePort;
        this.rolePersistencePort = rolePersistencePort;
    }

    /** Replaces username, display name and roles. The active flag is never changed here. */
    @Override
    @Transactional
    public UserResult execute(Long id, UserCommand command) {
        var user = userPersistencePort.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        if (userPersistencePort.existsByUsernameAndIdNot(command.username(), id)) {
            throw new DuplicateUsernameException(command.username());
        }
        var roles = AccessReferenceResolver.resolveRoles(rolePersistencePort, command.roleIds());
        return UserResult.from(userPersistencePort.saveChanges(
                user.withDetails(command.username(), command.displayName(), roles)));
    }
}
