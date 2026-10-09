package com.solgases.application.usecase;

import com.solgases.application.dto.CredentialProvisioningCommand;
import com.solgases.application.exception.CredentialAlreadyExistsException;
import com.solgases.application.exception.UnknownUsernameException;
import com.solgases.application.port.in.ProvisionUserCredentialUseCase;
import com.solgases.application.port.out.CredentialPersistencePort;
import com.solgases.application.port.out.PasswordHashEncoder;
import com.solgases.application.port.out.UserPersistencePort;
import com.solgases.domain.model.User;
import java.nio.CharBuffer;
import org.springframework.transaction.annotation.Transactional;

/** Stores the first credential of an existing user. An existing credential is never overwritten. */
public class ProvisionUserCredentialService implements ProvisionUserCredentialUseCase {

    private final UserPersistencePort userPersistencePort;
    private final CredentialPersistencePort credentialPersistencePort;
    private final PasswordHashEncoder passwordHashEncoder;

    public ProvisionUserCredentialService(UserPersistencePort userPersistencePort,
            CredentialPersistencePort credentialPersistencePort, PasswordHashEncoder passwordHashEncoder) {
        this.userPersistencePort = userPersistencePort;
        this.credentialPersistencePort = credentialPersistencePort;
        this.passwordHashEncoder = passwordHashEncoder;
    }

    @Override
    @Transactional
    public Long execute(CredentialProvisioningCommand command) {
        User user = userPersistencePort.findByUsername(command.username())
                .orElseThrow(UnknownUsernameException::new);
        if (credentialPersistencePort.existsByUserId(user.id())) {
            throw new CredentialAlreadyExistsException(user.id());
        }
        credentialPersistencePort.saveNew(user.id(), passwordHashEncoder.encode(CharBuffer.wrap(command.password())));
        return user.id();
    }
}
