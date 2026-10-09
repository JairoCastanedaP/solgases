package com.solgases.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.solgases.application.dto.UserCommand;
import com.solgases.application.dto.UserResult;
import com.solgases.application.exception.DuplicateUsernameException;
import com.solgases.application.exception.RoleNotFoundException;
import com.solgases.application.exception.UserNotFoundException;
import com.solgases.application.port.out.RolePersistencePort;
import com.solgases.application.port.out.UserPersistencePort;
import com.solgases.domain.model.Permission;
import com.solgases.domain.model.Role;
import com.solgases.domain.model.User;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserUseCasesTest {

    private static final Instant CREATED = Instant.parse("2026-01-01T00:00:00Z");
    private static final Permission PERMISSION = new Permission(10L, "P_KEY", "p.code", CREATED, CREATED);
    private static final Role ROLE_A = new Role(1L, "A_KEY", "Role A", Set.of(PERMISSION), CREATED, CREATED);
    private static final Role ROLE_B = new Role(2L, "B_KEY", "Role B", Set.of(), CREATED, CREATED);

    @Mock
    private UserPersistencePort userPersistencePort;

    @Mock
    private RolePersistencePort rolePersistencePort;

    private CreateUserService createUserService;
    private ListUsersService listUsersService;
    private GetUserByIdService getUserByIdService;
    private UpdateUserService updateUserService;
    private ActivateUserService activateUserService;
    private DeactivateUserService deactivateUserService;

    @BeforeEach
    void setUp() {
        createUserService = new CreateUserService(userPersistencePort, rolePersistencePort);
        listUsersService = new ListUsersService(userPersistencePort);
        getUserByIdService = new GetUserByIdService(userPersistencePort);
        updateUserService = new UpdateUserService(userPersistencePort, rolePersistencePort);
        activateUserService = new ActivateUserService(userPersistencePort);
        deactivateUserService = new DeactivateUserService(userPersistencePort);
    }

    private static User user(boolean active, Set<Role> roles) {
        return new User(5L, "jdoe", "John Doe", active, roles, CREATED, CREATED);
    }

    // Create

    @Test
    void createBuildsActiveUserWithResolvedRoles() {
        when(rolePersistencePort.findAllByIds(Set.of(1L, 2L))).thenReturn(List.of(ROLE_A, ROLE_B));
        when(userPersistencePort.saveNew(any(User.class))).thenReturn(user(true, Set.of(ROLE_A, ROLE_B)));

        UserResult result = createUserService.execute(new UserCommand("jdoe", "John Doe", Set.of(1L, 2L)));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userPersistencePort).saveNew(captor.capture());
        assertThat(captor.getValue().id()).isNull();
        assertThat(captor.getValue().active()).isTrue();
        assertThat(captor.getValue().roleIds()).containsExactlyInAnyOrder(1L, 2L);
        assertThat(result.active()).isTrue();
        assertThat(result.roles()).extracting("id").containsExactly(1L, 2L);
        assertThat(result.roles().getFirst().permissions()).extracting("key").containsExactly("P_KEY");
    }

    @Test
    void createWithoutRolesDoesNotQueryRoles() {
        when(userPersistencePort.saveNew(any(User.class))).thenReturn(user(true, Set.of()));

        assertThat(createUserService.execute(new UserCommand("jdoe", "John Doe", Set.of())).roles()).isEmpty();
        verifyNoInteractions(rolePersistencePort);
    }

    @Test
    void createWithDuplicateUsernameThrowsConflictWithoutSaving() {
        when(userPersistencePort.existsByUsername("jdoe")).thenReturn(true);

        assertThatThrownBy(() -> createUserService.execute(new UserCommand("jdoe", "John Doe", Set.of())))
                .isInstanceOf(DuplicateUsernameException.class);
        verify(userPersistencePort, never()).saveNew(any());
    }

    @Test
    void createWithUnknownRolesThrowsNotFoundListingMissingIds() {
        when(rolePersistencePort.findAllByIds(Set.of(1L, 7L, 8L))).thenReturn(List.of(ROLE_A));

        assertThatThrownBy(() -> createUserService.execute(new UserCommand("jdoe", "John Doe", Set.of(1L, 7L, 8L))))
                .isInstanceOf(RoleNotFoundException.class)
                .hasMessage("Roles not found with ids [7, 8]");
        verify(userPersistencePort, never()).saveNew(any());
    }

    // Read

    @Test
    void listReturnsActiveAndInactiveUsers() {
        when(userPersistencePort.findAll()).thenReturn(List.of(user(true, Set.of()), user(false, Set.of(ROLE_B))));

        assertThat(listUsersService.execute()).extracting(UserResult::active).containsExactly(true, false);
    }

    @Test
    void findByIdReturnsUserOrThrowsNotFound() {
        when(userPersistencePort.findById(5L)).thenReturn(Optional.of(user(true, Set.of(ROLE_A))));
        when(userPersistencePort.findById(99L)).thenReturn(Optional.empty());

        assertThat(getUserByIdService.execute(5L).username()).isEqualTo("jdoe");
        assertThatThrownBy(() -> getUserByIdService.execute(99L)).isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with id 99");
    }

    // Update

    @Test
    void updateReplacesDetailsAndRolesButKeepsActiveFlag() {
        User inactive = user(false, Set.of(ROLE_A));
        when(userPersistencePort.findById(5L)).thenReturn(Optional.of(inactive));
        when(rolePersistencePort.findAllByIds(Set.of(2L))).thenReturn(List.of(ROLE_B));
        User expected = inactive.withDetails("jdoe2", "Johnny", Set.of(ROLE_B));
        when(userPersistencePort.saveChanges(expected)).thenReturn(expected);

        UserResult result = updateUserService.execute(5L, new UserCommand("jdoe2", "Johnny", Set.of(2L)));

        assertThat(result.active()).isFalse();
        assertThat(result.username()).isEqualTo("jdoe2");
        assertThat(result.roles()).extracting("id").containsExactly(2L);
    }

    @Test
    void updateWithEmptyRolesRemovesAllRoles() {
        User current = user(true, Set.of(ROLE_A));
        when(userPersistencePort.findById(5L)).thenReturn(Optional.of(current));
        User expected = current.withDetails("jdoe", "John Doe", Set.of());
        when(userPersistencePort.saveChanges(expected)).thenReturn(expected);

        assertThat(updateUserService.execute(5L, new UserCommand("jdoe", "John Doe", Set.of())).roles()).isEmpty();
    }

    @Test
    void updateAllowsKeepingItsOwnUsername() {
        User current = user(true, Set.of());
        when(userPersistencePort.findById(5L)).thenReturn(Optional.of(current));
        when(userPersistencePort.saveChanges(current)).thenReturn(current);

        assertThat(updateUserService.execute(5L, new UserCommand("jdoe", "John Doe", Set.of())).username())
                .isEqualTo("jdoe");
        verify(userPersistencePort).existsByUsernameAndIdNot("jdoe", 5L);
    }

    @Test
    void updateThrowsNotFoundForMissingUserOrRole() {
        when(userPersistencePort.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> updateUserService.execute(99L, new UserCommand("x", "X", Set.of())))
                .isInstanceOf(UserNotFoundException.class);

        when(userPersistencePort.findById(5L)).thenReturn(Optional.of(user(true, Set.of())));
        when(rolePersistencePort.findAllByIds(Set.of(3L))).thenReturn(List.of());
        assertThatThrownBy(() -> updateUserService.execute(5L, new UserCommand("jdoe", "John Doe", Set.of(3L))))
                .isInstanceOf(RoleNotFoundException.class);
        verify(userPersistencePort, never()).saveChanges(any());
    }

    @Test
    void updateWithUsernameOfAnotherUserThrowsConflict() {
        when(userPersistencePort.findById(5L)).thenReturn(Optional.of(user(true, Set.of())));
        when(userPersistencePort.existsByUsernameAndIdNot("taken", 5L)).thenReturn(true);

        assertThatThrownBy(() -> updateUserService.execute(5L, new UserCommand("taken", "X", Set.of())))
                .isInstanceOf(DuplicateUsernameException.class);
        verify(userPersistencePort, never()).saveChanges(any());
    }

    // Activate / deactivate

    @Test
    void activateAndDeactivateChangeOnlyTheActiveFlag() {
        User active = user(true, Set.of(ROLE_A));
        User inactive = active.deactivate();
        when(userPersistencePort.findById(5L)).thenReturn(Optional.of(inactive));
        when(userPersistencePort.saveChanges(active)).thenReturn(active);
        assertThat(activateUserService.execute(5L)).isEqualTo(UserResult.from(active));

        when(userPersistencePort.findById(6L)).thenReturn(Optional.of(active));
        when(userPersistencePort.saveChanges(inactive)).thenReturn(inactive);
        assertThat(deactivateUserService.execute(6L)).isEqualTo(UserResult.from(inactive));
    }

    @Test
    void activateAndDeactivateThrowNotFoundWhenMissing() {
        when(userPersistencePort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activateUserService.execute(99L)).isInstanceOf(UserNotFoundException.class);
        assertThatThrownBy(() -> deactivateUserService.execute(99L)).isInstanceOf(UserNotFoundException.class);
    }
}
