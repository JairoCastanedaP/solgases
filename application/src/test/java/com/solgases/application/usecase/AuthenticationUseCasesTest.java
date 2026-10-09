package com.solgases.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.solgases.application.dto.AuthenticatedUser;
import com.solgases.application.dto.AuthenticationCommand;
import com.solgases.application.dto.StoredCredential;
import com.solgases.application.dto.UserAccess;
import com.solgases.application.exception.AuthenticationFailedException;
import com.solgases.application.port.out.CredentialPersistencePort;
import com.solgases.application.port.out.PasswordHashVerifier;
import com.solgases.application.port.out.UserPersistencePort;
import com.solgases.domain.model.Permission;
import com.solgases.domain.model.Role;
import com.solgases.domain.model.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthenticationUseCasesTest {

    private static final String HASH = "stored-hash";

    @Mock
    private CredentialPersistencePort credentialPersistencePort;
    @Mock
    private UserPersistencePort userPersistencePort;
    @Mock
    private PasswordHashVerifier passwordHashVerifier;

    private AuthenticateUserService authenticate;
    private GetUserAccessService getUserAccess;

    @BeforeEach
    void setUp() {
        authenticate = new AuthenticateUserService(credentialPersistencePort, userPersistencePort,
                passwordHashVerifier);
        getUserAccess = new GetUserAccessService(userPersistencePort);
    }

    private static User user(boolean active, Role... roles) {
        return new User(5L, "jdoe", "John Doe", active, Set.of(roles), null, null);
    }

    @Test
    void passwordAboveTheMaximumFailsGenericallyWithoutLookupOrHashing() {
        // 129 ASCII characters, and 129 supplementary code points (258 UTF-16 units)
        for (String oversized : List.of("x".repeat(129), "\uD83D\uDD12".repeat(129))) {
            assertThatThrownBy(() -> authenticate.execute(new AuthenticationCommand("jdoe", oversized)))
                    .isExactlyInstanceOf(AuthenticationFailedException.class);
        }

        verifyNoInteractions(credentialPersistencePort, passwordHashVerifier, userPersistencePort);
    }

    @Test
    void activeUserWithMatchingPasswordIsAuthenticated() {
        when(credentialPersistencePort.findByUsername("jdoe")).thenReturn(Optional.of(new StoredCredential(5L, HASH)));
        when(passwordHashVerifier.matches("secret-value", HASH)).thenReturn(true);
        when(userPersistencePort.findById(5L)).thenReturn(Optional.of(user(true)));

        AuthenticatedUser result = authenticate.execute(new AuthenticationCommand("jdoe", "secret-value"));

        assertThat(result).isEqualTo(new AuthenticatedUser(5L));
    }

    @Test
    void unknownUsernameFailsAfterADummyVerification() {
        when(credentialPersistencePort.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticate.execute(new AuthenticationCommand("ghost", "secret-value")))
                .isExactlyInstanceOf(AuthenticationFailedException.class)
                .hasMessage("Invalid username or password");
        verify(passwordHashVerifier).verifyAgainstDummyHash("secret-value");
        verify(userPersistencePort, never()).findById(any());
    }

    @Test
    void wrongPasswordFailsWithTheSameMessage() {
        when(credentialPersistencePort.findByUsername("jdoe")).thenReturn(Optional.of(new StoredCredential(5L, HASH)));
        when(passwordHashVerifier.matches("wrong-value", HASH)).thenReturn(false);

        assertThatThrownBy(() -> authenticate.execute(new AuthenticationCommand("jdoe", "wrong-value")))
                .isExactlyInstanceOf(AuthenticationFailedException.class)
                .hasMessage("Invalid username or password");
        verify(userPersistencePort, never()).findById(any());
    }

    @Test
    void inactiveOrMissingUserFailsEvenWithTheRightPassword() {
        when(credentialPersistencePort.findByUsername("jdoe")).thenReturn(Optional.of(new StoredCredential(5L, HASH)));
        when(passwordHashVerifier.matches("secret-value", HASH)).thenReturn(true);
        when(userPersistencePort.findById(5L)).thenReturn(Optional.of(user(false)), Optional.empty());

        for (int attempt = 0; attempt < 2; attempt++) {
            assertThatThrownBy(() -> authenticate.execute(new AuthenticationCommand("jdoe", "secret-value")))
                    .isExactlyInstanceOf(AuthenticationFailedException.class);
        }
    }

    @Test
    void commandsAndCredentialsNeverPrintSecrets() {
        assertThat(new AuthenticationCommand("jdoe", "secret-value").toString()).doesNotContain("secret-value");
        assertThat(new StoredCredential(5L, HASH).toString()).doesNotContain(HASH);
    }

    @Test
    void userAccessCombinesThePermissionKeysOfAllRoles() {
        Permission read = new Permission(1L, "P_READ", "editable.read", null, null);
        Permission write = new Permission(2L, "P_WRITE", "editable.write", null, null);
        Role reader = new Role(10L, "R_READER", "Reader", Set.of(read), null, null);
        Role editor = new Role(11L, "R_EDITOR", "Editor", Set.of(read, write), null, null);
        when(userPersistencePort.findById(5L)).thenReturn(Optional.of(user(true, reader, editor)));

        assertThat(getUserAccess.execute(5L))
                .contains(new UserAccess(5L, true, Set.of("P_READ", "P_WRITE")));
    }

    @Test
    void userAccessReportsInactiveUsersAndIsEmptyForMissingOnes() {
        when(userPersistencePort.findById(5L)).thenReturn(Optional.of(user(false)));
        when(userPersistencePort.findById(6L)).thenReturn(Optional.empty());

        assertThat(getUserAccess.execute(5L)).contains(new UserAccess(5L, false, Set.of()));
        assertThat(getUserAccess.execute(6L)).isEmpty();
    }
}
