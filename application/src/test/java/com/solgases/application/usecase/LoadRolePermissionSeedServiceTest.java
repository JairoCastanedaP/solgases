package com.solgases.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.solgases.application.dto.PermissionSeed;
import com.solgases.application.dto.RolePermissionSeedCatalog;
import com.solgases.application.dto.RolePermissionSeedResult;
import com.solgases.application.dto.RoleSeed;
import com.solgases.application.port.out.PermissionPersistencePort;
import com.solgases.application.port.out.RolePersistencePort;
import com.solgases.domain.model.Permission;
import com.solgases.domain.model.Role;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Uses neutral test data only: the real catalog content is still pending definition. */
@ExtendWith(MockitoExtension.class)
class LoadRolePermissionSeedServiceTest {

    private static final Permission P1 = new Permission(1L, "TEST_P1", "test.p1", null, null);
    private static final Permission P2 = new Permission(2L, "TEST_P2", "test.p2", null, null);

    @Mock
    private RolePersistencePort rolePersistencePort;

    @Mock
    private PermissionPersistencePort permissionPersistencePort;

    private LoadRolePermissionSeedService service;

    @BeforeEach
    void setUp() {
        service = new LoadRolePermissionSeedService(rolePersistencePort, permissionPersistencePort);
    }

    private static RolePermissionSeedCatalog catalog() {
        return new RolePermissionSeedCatalog(
                List.of(new PermissionSeed("TEST_P1", "test.p1"), new PermissionSeed("TEST_P2", "test.p2")),
                List.of(new RoleSeed("TEST_ROLE", "Test role", Set.of("TEST_P1", "TEST_P2")),
                        new RoleSeed("TEST_EMPTY_ROLE", "Test role without permissions", Set.of())));
    }

    @Test
    void emptyCatalogCreatesNothing() {
        assertThat(service.execute(new RolePermissionSeedCatalog(List.of(), List.of())))
                .isEqualTo(new RolePermissionSeedResult(0, 0));
        verifyNoInteractions(rolePersistencePort, permissionPersistencePort);
    }

    @Test
    void firstLoadCreatesPermissionsAndRolesWithTheirSeedPermissions() {
        when(permissionPersistencePort.findAllByKeys(Set.of("TEST_P1", "TEST_P2"))).thenReturn(List.of(P1, P2));

        assertThat(service.execute(catalog())).isEqualTo(new RolePermissionSeedResult(2, 2));

        verify(permissionPersistencePort).saveNew(Permission.newPermission("TEST_P1", "test.p1"));
        verify(permissionPersistencePort).saveNew(Permission.newPermission("TEST_P2", "test.p2"));
        ArgumentCaptor<Role> roles = ArgumentCaptor.forClass(Role.class);
        verify(rolePersistencePort, org.mockito.Mockito.times(2)).saveNew(roles.capture());
        assertThat(roles.getAllValues().getFirst().permissionIds()).containsExactlyInAnyOrder(1L, 2L);
        assertThat(roles.getAllValues().get(1).permissions()).isEmpty();
    }

    @Test
    void repeatedLoadSkipsExistingRecordsByKeyAndNeverTouchesTheirData() {
        when(permissionPersistencePort.existsByKey(any())).thenReturn(true);
        when(rolePersistencePort.existsByKey(any())).thenReturn(true);

        assertThat(service.execute(catalog())).isEqualTo(new RolePermissionSeedResult(0, 0));

        verify(permissionPersistencePort, never()).saveNew(any());
        verify(permissionPersistencePort, never()).saveChanges(any());
        verify(rolePersistencePort, never()).saveNew(any());
        verify(rolePersistencePort, never()).saveChanges(any());
        // Existing roles keep their current permissions, even if they differ from the seed
        verify(permissionPersistencePort, never()).findAllByKeys(any());
    }

    @Test
    void roleReferencingUnknownPermissionKeyFails() {
        var catalog = new RolePermissionSeedCatalog(List.of(),
                List.of(new RoleSeed("TEST_ROLE", "Test role", Set.of("TEST_P1", "MISSING"))));
        when(permissionPersistencePort.findAllByKeys(Set.of("TEST_P1", "MISSING"))).thenReturn(List.of(P1));

        assertThatThrownBy(() -> service.execute(catalog))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("MISSING");
        verify(rolePersistencePort, never()).saveNew(any());
    }
}
