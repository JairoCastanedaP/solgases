package com.solgases.infrastructure.config;

import com.solgases.application.dto.RolePermissionSeedCatalog;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Content of the initial role/permission catalog.
 *
 * <p>PENDING: the concrete roles (key and name), permissions (key and code) and the initial permissions of each
 * role have not been defined yet (see docs/mvp1.md, Increment 5). Until they are, the catalog is intentionally
 * empty and the seed creates nothing. Do not add placeholder or invented entries here.
 */
@Configuration
public class InitialRolePermissionCatalog {

    @Bean
    RolePermissionSeedCatalog initialRolePermissionSeedCatalog() {
        return new RolePermissionSeedCatalog(List.of(), List.of());
    }
}
