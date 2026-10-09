package com.solgases.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.solgases.application.dto.PermissionCreateCommand;
import com.solgases.application.dto.PermissionResult;
import com.solgases.application.dto.PermissionUpdateCommand;
import com.solgases.application.exception.DuplicatePermissionCodeException;
import com.solgases.application.exception.DuplicatePermissionKeyException;
import com.solgases.application.exception.PermissionNotFoundException;
import com.solgases.application.port.out.PermissionPersistencePort;
import com.solgases.domain.model.Permission;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PermissionUseCasesTest {

    private static final Instant CREATED = Instant.parse("2026-01-01T00:00:00Z");
    private static final Permission PERMISSION = new Permission(1L, "P_KEY", "p.code", CREATED, CREATED);

    @Mock
    private PermissionPersistencePort permissionPersistencePort;

    private CreatePermissionService createPermissionService;
    private ListPermissionsService listPermissionsService;
    private GetPermissionByIdService getPermissionByIdService;
    private UpdatePermissionService updatePermissionService;

    @BeforeEach
    void setUp() {
        createPermissionService = new CreatePermissionService(permissionPersistencePort);
        listPermissionsService = new ListPermissionsService(permissionPersistencePort);
        getPermissionByIdService = new GetPermissionByIdService(permissionPersistencePort);
        updatePermissionService = new UpdatePermissionService(permissionPersistencePort);
    }

    @Test
    void createSavesNewPermissionWithKeyAndCode() {
        when(permissionPersistencePort.saveNew(Permission.newPermission("P_KEY", "p.code"))).thenReturn(PERMISSION);

        assertThat(createPermissionService.execute(new PermissionCreateCommand("P_KEY", "p.code")))
                .isEqualTo(PermissionResult.from(PERMISSION));
    }

    @Test
    void createWithDuplicateKeyOrCodeThrowsConflict() {
        when(permissionPersistencePort.existsByKey("P_KEY")).thenReturn(true);
        assertThatThrownBy(() -> createPermissionService.execute(new PermissionCreateCommand("P_KEY", "x")))
                .isInstanceOf(DuplicatePermissionKeyException.class);

        when(permissionPersistencePort.existsByCode("p.code")).thenReturn(true);
        assertThatThrownBy(() -> createPermissionService.execute(new PermissionCreateCommand("OTHER", "p.code")))
                .isInstanceOf(DuplicatePermissionCodeException.class);
        verify(permissionPersistencePort, never()).saveNew(any());
    }

    @Test
    void listAndFindById() {
        when(permissionPersistencePort.findAll()).thenReturn(List.of(PERMISSION));
        when(permissionPersistencePort.findById(1L)).thenReturn(Optional.of(PERMISSION));
        when(permissionPersistencePort.findById(99L)).thenReturn(Optional.empty());

        assertThat(listPermissionsService.execute()).containsExactly(PermissionResult.from(PERMISSION));
        assertThat(getPermissionByIdService.execute(1L).code()).isEqualTo("p.code");
        assertThatThrownBy(() -> getPermissionByIdService.execute(99L))
                .isInstanceOf(PermissionNotFoundException.class)
                .hasMessage("Permission not found with id 99");
    }

    @Test
    void updateChangesCodeButNeverTheKey() {
        when(permissionPersistencePort.findById(1L)).thenReturn(Optional.of(PERMISSION));
        Permission expected = PERMISSION.withCode("new.code");
        when(permissionPersistencePort.saveChanges(expected)).thenReturn(expected);

        PermissionResult result = updatePermissionService.execute(1L, new PermissionUpdateCommand("new.code"));

        assertThat(result.key()).isEqualTo("P_KEY");
        assertThat(result.code()).isEqualTo("new.code");
    }

    @Test
    void updateFailsForMissingPermissionOrDuplicateCode() {
        when(permissionPersistencePort.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> updatePermissionService.execute(99L, new PermissionUpdateCommand("x")))
                .isInstanceOf(PermissionNotFoundException.class);

        when(permissionPersistencePort.findById(1L)).thenReturn(Optional.of(PERMISSION));
        when(permissionPersistencePort.existsByCodeAndIdNot("taken", 1L)).thenReturn(true);
        assertThatThrownBy(() -> updatePermissionService.execute(1L, new PermissionUpdateCommand("taken")))
                .isInstanceOf(DuplicatePermissionCodeException.class);
        verify(permissionPersistencePort, never()).saveChanges(any());
    }
}
