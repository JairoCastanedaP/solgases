package com.solgases.infrastructure.config;

import com.solgases.application.dto.PermissionSeed;
import com.solgases.application.dto.RolePermissionSeedCatalog;
import com.solgases.application.dto.RoleSeed;
import java.util.List;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Content of the initial role/permission catalog approved for Increment 7: nine business permissions, read and
 * write per area, with inventory movements and adjustments kept apart. The permission codes and role names start
 * equal to their internal keys and remain editable; the keys never change. A role receives these permissions
 * only when it is created for the first time (see LoadRolePermissionSeedService).
 *
 * <p>Other roles, such as an inventory operator, are pending business definition and must not be added here
 * without approval.
 */
@Configuration
public class InitialRolePermissionCatalog {

    public static final String CATALOG_READ = "CATALOG_READ";
    public static final String CATALOG_WRITE = "CATALOG_WRITE";
    public static final String INVENTORY_READ = "INVENTORY_READ";
    public static final String INVENTORY_MOVE = "INVENTORY_MOVE";
    public static final String INVENTORY_ADJUST = "INVENTORY_ADJUST";
    public static final String USER_READ = "USER_READ";
    public static final String USER_WRITE = "USER_WRITE";
    public static final String ACCESS_READ = "ACCESS_READ";
    public static final String ACCESS_WRITE = "ACCESS_WRITE";

    public static final List<String> BUSINESS_PERMISSIONS = List.of(CATALOG_READ, CATALOG_WRITE, INVENTORY_READ,
            INVENTORY_MOVE, INVENTORY_ADJUST, USER_READ, USER_WRITE, ACCESS_READ, ACCESS_WRITE);

    /** Receives every business permission; USER_WRITE and ACCESS_WRITE are granted to no other role. */
    public static final String ADMIN = "ADMIN";
    /** Read-only access to the catalog and the inventory. */
    public static final String VIEWER = "VIEWER";

    @Bean
    RolePermissionSeedCatalog initialRolePermissionSeedCatalog() {
        return new RolePermissionSeedCatalog(
                BUSINESS_PERMISSIONS.stream().map(key -> new PermissionSeed(key, key)).toList(),
                List.of(
                        new RoleSeed(ADMIN, ADMIN, Set.copyOf(BUSINESS_PERMISSIONS)),
                        new RoleSeed(VIEWER, VIEWER, Set.of(CATALOG_READ, INVENTORY_READ))));
    }
}
