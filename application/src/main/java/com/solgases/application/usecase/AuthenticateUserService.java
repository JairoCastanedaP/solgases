package com.solgases.application.usecase;

import com.solgases.application.dto.AuthenticatedUser;
import com.solgases.application.dto.AuthenticationCommand;
import com.solgases.application.dto.PasswordPolicy;
import com.solgases.application.dto.StoredCredential;
import com.solgases.application.exception.AuthenticationFailedException;
import com.solgases.application.port.in.AuthenticateUserUseCase;
import com.solgases.application.port.out.CredentialPersistencePort;
import com.solgases.application.port.out.PasswordHashVerifier;
import com.solgases.application.port.out.UserPersistencePort;
import com.solgases.domain.model.User;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authenticates an internal user with username and password. Every failure (unknown username, wrong password,
 * inactive user, missing credential or a password longer than the policy maximum) is reported the same way.
 */
public class AuthenticateUserService implements AuthenticateUserUseCase {

    private final CredentialPersistencePort credentialPersistencePort;
    private final UserPersistencePort userPersistencePort;
    private final PasswordHashVerifier passwordHashVerifier;

    public AuthenticateUserService(CredentialPersistencePort credentialPersistencePort,
            UserPersistencePort userPersistencePort, PasswordHashVerifier passwordHashVerifier) {
        this.credentialPersistencePort = credentialPersistencePort;
        this.userPersistencePort = userPersistencePort;
        this.passwordHashVerifier = passwordHashVerifier;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthenticatedUser execute(AuthenticationCommand command) {
        // Oversized input is rejected before any lookup or Argon2 computation, with the same generic failure
        if (PasswordPolicy.exceedsMaxLength(command.password())) {
            throw new AuthenticationFailedException();
        }
        Optional<StoredCredential> credential = credentialPersistencePort.findByUsername(command.username());
        if (credential.isEmpty()) {
            passwordHashVerifier.verifyAgainstDummyHash(command.password());
            throw new AuthenticationFailedException();
        }
        if (!passwordHashVerifier.matches(command.password(), credential.get().passwordHash())) {
            throw new AuthenticationFailedException();
        }
        boolean active = userPersistencePort.findById(credential.get().userId()).map(User::active).orElse(false);
        if (!active) {
            throw new AuthenticationFailedException();
        }
        return new AuthenticatedUser(credential.get().userId());
    }
}
