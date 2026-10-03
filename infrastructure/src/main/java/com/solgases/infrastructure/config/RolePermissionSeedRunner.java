package com.solgases.infrastructure.config;

import com.solgases.application.dto.RolePermissionSeedCatalog;
import com.solgases.application.port.in.LoadRolePermissionSeedUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** Loads the initial roles and permissions on startup. Running it again creates no duplicates. */
@Component
public class RolePermissionSeedRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(RolePermissionSeedRunner.class);

    private final LoadRolePermissionSeedUseCase loadRolePermissionSeedUseCase;
    private final RolePermissionSeedCatalog catalog;

    public RolePermissionSeedRunner(LoadRolePermissionSeedUseCase loadRolePermissionSeedUseCase,
            RolePermissionSeedCatalog catalog) {
        this.loadRolePermissionSeedUseCase = loadRolePermissionSeedUseCase;
        this.catalog = catalog;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (catalog.isEmpty()) {
            log.info("Initial role/permission catalog is empty (pending definition); nothing to load");
            return;
        }
        var result = loadRolePermissionSeedUseCase.execute(catalog);
        log.info("Initial role/permission catalog loaded: {} permissions and {} roles created",
                result.createdPermissions(), result.createdRoles());
    }
}
