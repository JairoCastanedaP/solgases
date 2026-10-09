package com.solgases.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.solgases.application.dto.RoleCreateCommand;
import com.solgases.application.dto.RoleResult;
import com.solgases.application.dto.RoleUpdateCommand;
import com.solgases.application.exception.DuplicateRoleKeyException;
import com.solgases.application.exception.DuplicateRoleNameException;
import com.solgases.application.exception.PermissionNotFoundException;
import com.solgases.application.exception.RoleNotFoundException;
import com.solgases.application.port.out.PermissionPersistencePort;
import com.solgases.application.port.out.RolePersistencePort;
import com.solgases.domain.model.Permission;
import com.solgases.domain.model.Role;
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
class RoleUseCasesTest {

    private static final Instant CREATED = Instant.parse("2026-01-01T00:00:00Z");
    private static final Permission P1 = new Permission(1L, "P1", "p.one", CREATED, CREATED);
    private static final Permission P2 = new Permission(2L, "P2", "p.two", CREATED, CREATED);

    @Mock
    private RolePersistencePort rolePersistencePort;

    @Mock
    private PermissionPersistencePort permissionPersistencePort;

    private CreateRoleService createRoleService;
    private ListRolesService listRolesService;
    private GetRoleByIdService getRoleByIdService;
    private UpdateRoleService updateRoleService;

    @BeforeEach
    void setUp() {
        createRoleService = new CreateRoleService(rolePersistencePort, permissionPersistencePort);
        listRolesService = new ListRolesService(rolePersistencePort);
        getRoleByIdService = new GetRoleByIdService(rolePersistencePort);
        updateRoleService = new UpdateRoleService(rolePersistencePort, permissionPersistencePort);
    }

    private static Role role(String name, Set<Permission> permissions) {
        return new Role(3L, "R_KEY", name, permissions, CREATED, CREATED);
    }

    @Test
    void createSavesRoleWithKeyNameAndResolvedPermissions() {
        when(permissionPersistencePort.findAllByIds(Set.of(1L, 2L))).thenReturn(List.of(P2, P1));
        when(rolePersistencePort.saveNew(any(Role.class))).thenReturn(role("Role", Set.of(P1, P2)));

        RoleResult result = createRoleService.execute(new RoleCreateCommand("R_KEY", "Role", Set.of(1L, 2L)));

        ArgumentCaptor<Role> captor = ArgumentCaptor.forClass(Role.class);
        verify(rolePersistencePort).saveNew(captor.capture());
        assertThat(captor.getValue().key()).isEqualTo("R_KEY");
        assertThat(captor.getValue().permissionIds()).containsExactlyInAnyOrder(1L, 2L);
        assertThat(result.permissions()).extracting("id").containsExactly(1L, 2L);
    }

    @Test
    void createAllowsRoleWithoutPermissions() {
        when(rolePersistencePort.saveNew(any(Role.class))).thenReturn(role("Role", Set.of()));

        assertThat(createRoleService.execute(new RoleCreateCommand("R_KEY", "Role", Set.of())).permissions()).isEmpty();
        verifyNoInteractions(permissionPersistencePort);
    }

    @Test
    void createWithDuplicateKeyOrNameThrowsConflict() {
        when(rolePersistencePort.existsByKey("R_KEY")).thenReturn(true);
        assertThatThrownBy(() -> createRoleService.execute(new RoleCreateCommand("R_KEY", "Role", Set.of())))
                .isInstanceOf(DuplicateRoleKeyException.class);

        when(rolePersistencePort.existsByName("Taken")).thenReturn(true);
        assertThatThrownBy(() -> createRoleService.execute(new RoleCreateCommand("OTHER", "Taken", Set.of())))
                .isInstanceOf(DuplicateRoleNameException.class);
        verify(rolePersistencePort, never()).saveNew(any());
    }

    @Test
    void createWithUnknownPermissionThrowsNotFound() {
        when(permissionPersistencePort.findAllByIds(Set.of(1L, 9L))).thenReturn(List.of(P1));

        assertThatThrownBy(() -> createRoleService.execute(new RoleCreateCommand("R_KEY", "Role", Set.of(1L, 9L))))
                .isInstanceOf(PermissionNotFoundException.class)
                .hasMessage("Permissions not found with ids [9]");
        verify(rolePersistencePort, never()).saveNew(any());
    }

    @Test
    void listAndFindByIdReturnRolesWithPermissions() {
        when(rolePersistencePort.findAll()).thenReturn(List.of(role("Role", Set.of(P1))));
        when(rolePersistencePort.findById(3L)).thenReturn(Optional.of(role("Role", Set.of(P1))));
        when(rolePersistencePort.findById(99L)).thenReturn(Optional.empty());

        assertThat(listRolesService.execute()).singleElement()
                .satisfies(r -> assertThat(r.permissions()).extracting("code").containsExactly("p.one"));
        assertThat(getRoleByIdService.execute(3L).key()).isEqualTo("R_KEY");
        assertThatThrownBy(() -> getRoleByIdService.execute(99L)).isInstanceOf(RoleNotFoundException.class);
    }

    @Test
    void updateReplacesNameAndPermissionsButNeverTheKey() {
        Role current = role("Old", Set.of(P1));
        when(rolePersistencePort.findById(3L)).thenReturn(Optional.of(current));
        when(permissionPersistencePort.findAllByIds(Set.of(2L))).thenReturn(List.of(P2));
        Role expected = current.withDetails("New", Set.of(P2));
        when(rolePersistencePort.saveChanges(expected)).thenReturn(expected);

        RoleResult result = updateRoleService.execute(3L, new RoleUpdateCommand("New", Set.of(2L)));

        assertThat(result.key()).isEqualTo("R_KEY");
        assertThat(result.name()).isEqualTo("New");
        assertThat(result.permissions()).extracting("id").containsExactly(2L);
    }

    @Test
    void updateWithEmptyPermissionsRemovesThemAll() {
        Role current = role("Role", Set.of(P1, P2));
        when(rolePersistencePort.findById(3L)).thenReturn(Optional.of(current));
        Role expected = current.withDetails("Role", Set.of());
        when(rolePersistencePort.saveChanges(expected)).thenReturn(expected);

        assertThat(updateRoleService.execute(3L, new RoleUpdateCommand("Role", Set.of())).permissions()).isEmpty();
    }

    @Test
    void updateFailsForMissingRoleDuplicateNameOrUnknownPermission() {
        when(rolePersistencePort.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> updateRoleService.execute(99L, new RoleUpdateCommand("X", Set.of())))
                .isInstanceOf(RoleNotFoundException.class);

        when(rolePersistencePort.findById(3L)).thenReturn(Optional.of(role("Role", Set.of())));
        when(rolePersistencePort.existsByNameAndIdNot("Taken", 3L)).thenReturn(true);
        assertThatThrownBy(() -> updateRoleService.execute(3L, new RoleUpdateCommand("Taken", Set.of())))
                .isInstanceOf(DuplicateRoleNameException.class);

        when(permissionPersistencePort.findAllByIds(Set.of(9L))).thenReturn(List.of());
        assertThatThrownBy(() -> updateRoleService.execute(3L, new RoleUpdateCommand("Role", Set.of(9L))))
                .isInstanceOf(PermissionNotFoundException.class);
        verify(rolePersistencePort, never()).saveChanges(any());
    }
}
