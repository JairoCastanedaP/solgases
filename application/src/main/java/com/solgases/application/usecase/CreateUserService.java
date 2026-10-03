package com.solgases.application.usecase;

import com.solgases.application.dto.UserCommand;
import com.solgases.application.dto.UserResult;
import com.solgases.application.exception.DuplicateUsernameException;
import com.solgases.application.port.in.CreateUserUseCase;
import com.solgases.application.port.out.RolePersistencePort;
import com.solgases.application.port.out.UserPersistencePort;
import com.solgases.domain.model.User;
import org.springframework.transaction.annotation.Transactional;

public class CreateUserService implements CreateUserUseCase {

    private final UserPersistencePort userPersistencePort;
    private final RolePersistencePort rolePersistencePort;

    public CreateUserService(UserPersistencePort userPersistencePort, RolePersistencePort rolePersistencePort) {
        this.userPersistencePort = userPersistencePort;
        this.rolePersistencePort = rolePersistencePort;
    }

    @Override
    @Transactional
    public UserResult execute(UserCommand command) {
        if (userPersistencePort.existsByUsername(command.username())) {
            throw new DuplicateUsernameException(command.username());
        }
        var roles = AccessReferenceResolver.resolveRoles(rolePersistencePort, command.roleIds());
        return UserResult.from(userPersistencePort.saveNew(
                User.newUser(command.username(), command.displayName(), roles)));
    }
}
