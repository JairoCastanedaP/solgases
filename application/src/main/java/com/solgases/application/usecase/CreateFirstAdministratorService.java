package com.solgases.application.usecase;

import com.solgases.application.dto.FirstAdministratorCommand;
import com.solgases.application.exception.DuplicateUsernameException;
import com.solgases.application.exception.FirstAdministratorAlreadyExistsException;
import com.solgases.application.port.in.CreateFirstAdministratorUseCase;
import com.solgases.application.port.out.CredentialPersistencePort;
import com.solgases.application.port.out.PasswordHashEncoder;
import com.solgases.application.port.out.RolePersistencePort;
import com.solgases.application.port.out.UserPersistencePort;
import com.solgases.domain.model.Role;
import com.solgases.domain.model.User;
import java.nio.CharBuffer;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates the first administrator: a new active user with the administrator role and its credential. It refuses
 * to run when an active user already has that role, so it cannot be used to add administrators later.
 */
public class CreateFirstAdministratorService implements CreateFirstAdministratorUseCase {

    private final UserPersistencePort userPersistencePort;
    private final RolePersistencePort rolePersistencePort;
    private final CredentialPersistencePort credentialPersistencePort;
    private final PasswordHashEncoder passwordHashEncoder;
    private final String administratorRoleKey;

    public CreateFirstAdministratorService(UserPersistencePort userPersistencePort,
            RolePersistencePort rolePersistencePort, CredentialPersistencePort credentialPersistencePort,
            PasswordHashEncoder passwordHashEncoder, String administratorRoleKey) {
        this.userPersistencePort = userPersistencePort;
        this.rolePersistencePort = rolePersistencePort;
        this.credentialPersistencePort = credentialPersistencePort;
        this.passwordHashEncoder = passwordHashEncoder;
        this.administratorRoleKey = administratorRoleKey;
    }

    @Override
    @Transactional
    public Long execute(FirstAdministratorCommand command) {
        if (userPersistencePort.existsActiveUserWithRole(administratorRoleKey)) {
            throw new FirstAdministratorAlreadyExistsException();
        }
        Role administratorRole = rolePersistencePort.findByKey(administratorRoleKey)
                .orElseThrow(() -> new IllegalStateException(
                        "The administrator role '" + administratorRoleKey + "' has not been loaded"));
        if (userPersistencePort.existsByUsername(command.username())) {
            throw new DuplicateUsernameException(command.username());
        }
        String passwordHash = passwordHashEncoder.encode(CharBuffer.wrap(command.password()));
        User administrator = userPersistencePort.saveNew(
                User.newUser(command.username(), command.displayName(), Set.of(administratorRole)));
        credentialPersistencePort.saveNew(administrator.id(), passwordHash);
        return administrator.id();
    }
}
