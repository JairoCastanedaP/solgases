package com.solgases.infrastructure.config;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.solgases.application.dto.PermissionSeed;
import com.solgases.application.dto.RolePermissionSeedCatalog;
import com.solgases.application.dto.RolePermissionSeedResult;
import com.solgases.application.port.in.LoadRolePermissionSeedUseCase;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.DefaultApplicationArguments;

@ExtendWith(MockitoExtension.class)
class RolePermissionSeedRunnerTest {

    @Mock
    private LoadRolePermissionSeedUseCase loadRolePermissionSeedUseCase;

    @Test
    void emptyCatalogDoesNotInvokeTheSeed() {
        new RolePermissionSeedRunner(loadRolePermissionSeedUseCase, new RolePermissionSeedCatalog(List.of(), List.of()))
                .run(new DefaultApplicationArguments());

        verifyNoInteractions(loadRolePermissionSeedUseCase);
    }

    @Test
    void nonEmptyCatalogIsLoaded() {
        var catalog = new RolePermissionSeedCatalog(List.of(new PermissionSeed("TEST_P", "test.p")), List.of());
        when(loadRolePermissionSeedUseCase.execute(catalog)).thenReturn(new RolePermissionSeedResult(1, 0));

        new RolePermissionSeedRunner(loadRolePermissionSeedUseCase, catalog).run(new DefaultApplicationArguments());

        verify(loadRolePermissionSeedUseCase).execute(catalog);
    }
}
