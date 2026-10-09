package com.solgases.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.solgases.application.dto.CredentialProvisioningCommand;
import com.solgases.application.dto.FirstAdministratorCommand;
import com.solgases.application.exception.CredentialAlreadyExistsException;
import com.solgases.application.exception.DuplicateUsernameException;
import com.solgases.application.exception.FirstAdministratorAlreadyExistsException;
import com.solgases.application.exception.UnknownUsernameException;
import com.solgases.application.port.out.CredentialPersistencePort;
import com.solgases.application.port.out.PasswordHashEncoder;
import com.solgases.application.port.out.RolePersistencePort;
import com.solgases.application.port.out.UserPersistencePort;
import com.solgases.domain.model.Role;
import com.solgases.domain.model.User;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Synthetic data only: no real user or password is involved. */
@ExtendWith(MockitoExtension.class)
class CredentialUseCasesTest {

    private static final String ADMIN_KEY = "TEST_ADMIN";
    private static final Role ADMIN_ROLE = new Role(1L, ADMIN_KEY, "Test admin", Set.of(), null, null);
    private static final String HASH = "synthetic-hash";
    private static final String PASSWORD = "synthetic-password-01";

    @Mock
    private UserPersistencePort userPersistencePort;
    @Mock
    private RolePersistencePort rolePersistencePort;
    @Mock
    private CredentialPersistencePort credentialPersistencePort;
    @Mock
    private PasswordHashEncoder passwordHashEncoder;

    private CreateFirstAdministratorService createFirstAdministrator;
    private ProvisionUserCredentialService provisionUserCredential;

    @BeforeEach
    void setUp() {
        createFirstAdministrator = new CreateFirstAdministratorService(userPersistencePort, rolePersistencePort,
                credentialPersistencePort, passwordHashEncoder, ADMIN_KEY);
        provisionUserCredential = new ProvisionUserCredentialService(userPersistencePort, credentialPersistencePort,
                passwordHashEncoder);
    }

    private static FirstAdministratorCommand firstAdministrator() {
        return new FirstAdministratorCommand("synthetic-admin", "Synthetic Admin", PASSWORD.toCharArray());
    }

    @Test
    void firstAdministratorIsCreatedWithTheAdministratorRoleAndOnlyThePasswordHash() {
        when(userPersistencePort.existsActiveUserWithRole(ADMIN_KEY)).thenReturn(false);
        when(rolePersistencePort.findByKey(ADMIN_KEY)).thenReturn(Optional.of(ADMIN_ROLE));
        when(userPersistencePort.existsByUsername("synthetic-admin")).thenReturn(false);
        when(passwordHashEncoder.encode(any())).thenReturn(HASH);
        when(userPersistencePort.saveNew(any())).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return new User(7L, user.username(), user.displayName(), user.active(), user.roles(), null, null);
        });

        Long userId = createFirstAdministrator.execute(firstAdministrator());

        assertThat(userId).isEqualTo(7L);
        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userPersistencePort).saveNew(saved.capture());
        assertThat(saved.getValue().active()).isTrue();
        assertThat(saved.getValue().roles()).containsExactly(ADMIN_ROLE);
        ArgumentCaptor<CharSequence> rawPassword = ArgumentCaptor.forClass(CharSequence.class);
        verify(passwordHashEncoder).encode(rawPassword.capture());
        assertThat(rawPassword.getValue().toString()).isEqualTo(PASSWORD);
        verify(credentialPersistencePort).saveNew(7L, HASH);
    }

    @Test
    void firstAdministratorIsRefusedWhenAnActiveAdministratorExists() {
        when(userPersistencePort.existsActiveUserWithRole(ADMIN_KEY)).thenReturn(true);

        assertThatThrownBy(() -> createFirstAdministrator.execute(firstAdministrator()))
                .isInstanceOf(FirstAdministratorAlreadyExistsException.class);

        verify(userPersistencePort, never()).saveNew(any());
        verify(credentialPersistencePort, never()).saveNew(any(), anyString());
        verify(passwordHashEncoder, never()).encode(any());
    }

    @Test
    void firstAdministratorFailsWhenTheAdministratorRoleIsNotLoaded() {
        when(userPersistencePort.existsActiveUserWithRole(ADMIN_KEY)).thenReturn(false);
        when(rolePersistencePort.findByKey(ADMIN_KEY)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> createFirstAdministrator.execute(firstAdministrator()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(ADMIN_KEY);
        verify(userPersistencePort, never()).saveNew(any());
    }

    @Test
    void firstAdministratorRejectsAnExistingUsername() {
        when(userPersistencePort.existsActiveUserWithRole(ADMIN_KEY)).thenReturn(false);
        when(rolePersistencePort.findByKey(ADMIN_KEY)).thenReturn(Optional.of(ADMIN_ROLE));
        when(userPersistencePort.existsByUsername("synthetic-admin")).thenReturn(true);

        assertThatThrownBy(() -> createFirstAdministrator.execute(firstAdministrator()))
                .isInstanceOf(DuplicateUsernameException.class);
        verify(credentialPersistencePort, never()).saveNew(any(), anyString());
    }

    @Test
    void credentialIsProvisionedForAnExistingUserWithoutCredential() {
        when(userPersistencePort.findByUsername("synthetic-user"))
                .thenReturn(Optional.of(new User(9L, "synthetic-user", "Synthetic User", true, Set.of(), null, null)));
        when(credentialPersistencePort.existsByUserId(9L)).thenReturn(false);
        when(passwordHashEncoder.encode(any())).thenReturn(HASH);

        Long userId = provisionUserCredential.execute(
                new CredentialProvisioningCommand("synthetic-user", PASSWORD.toCharArray()));

        assertThat(userId).isEqualTo(9L);
        verify(credentialPersistencePort).saveNew(9L, HASH);
    }

    @Test
    void existingCredentialIsNeverOverwritten() {
        when(userPersistencePort.findByUsername("synthetic-user"))
                .thenReturn(Optional.of(new User(9L, "synthetic-user", "Synthetic User", true, Set.of(), null, null)));
        when(credentialPersistencePort.existsByUserId(9L)).thenReturn(true);

        assertThatThrownBy(() -> provisionUserCredential.execute(
                new CredentialProvisioningCommand("synthetic-user", PASSWORD.toCharArray())))
                .isInstanceOf(CredentialAlreadyExistsException.class)
                .hasMessageContaining("password change flow");
        verify(credentialPersistencePort, never()).saveNew(any(), anyString());
        verify(passwordHashEncoder, never()).encode(any());
    }

    @Test
    void provisioningFailsForAnUnknownUsernameWithoutRevealingIt() {
        when(userPersistencePort.findByUsername("synthetic-missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> provisionUserCredential.execute(
                new CredentialProvisioningCommand("synthetic-missing", PASSWORD.toCharArray())))
                .isInstanceOf(UnknownUsernameException.class)
                .hasMessageNotContaining("synthetic-missing");
    }

    @Test
    void passwordsOutsideThePolicyAreRejectedBeforeAnyUseCaseRuns() {
        assertThatThrownBy(() -> new FirstAdministratorCommand("a", "b", new char[0]))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new FirstAdministratorCommand("a", "b", "x".repeat(14).toCharArray()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new CredentialProvisioningCommand("a", "x".repeat(129).toCharArray()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new CredentialProvisioningCommand("a", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void passwordIsHashedExactlyAsEnteredWithSpacesAndUnicode() {
        String exact = "  contraseña \uD83D\uDD12 con espacios  ";
        when(userPersistencePort.findByUsername("synthetic-user"))
                .thenReturn(Optional.of(new User(9L, "synthetic-user", "Synthetic User", true, Set.of(), null, null)));
        when(credentialPersistencePort.existsByUserId(9L)).thenReturn(false);
        when(passwordHashEncoder.encode(any())).thenReturn(HASH);

        provisionUserCredential.execute(new CredentialProvisioningCommand("synthetic-user", exact.toCharArray()));

        ArgumentCaptor<CharSequence> rawPassword = ArgumentCaptor.forClass(CharSequence.class);
        verify(passwordHashEncoder).encode(rawPassword.capture());
        assertThat(rawPassword.getValue().toString()).isEqualTo(exact);
    }

    @Test
    void passwordsNeverAppearInToString() {
        assertThat(firstAdministrator().toString()).doesNotContain(PASSWORD).contains("****");
        assertThat(new CredentialProvisioningCommand("a", PASSWORD.toCharArray()).toString())
                .doesNotContain(PASSWORD);
    }
}
