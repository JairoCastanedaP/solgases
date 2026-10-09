package com.solgases.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.solgases.application.dto.PermissionSeed;
import com.solgases.application.dto.RolePermissionSeedCatalog;
import com.solgases.application.dto.RoleSeed;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class InitialRolePermissionCatalogTest {

    private final RolePermissionSeedCatalog catalog = new InitialRolePermissionCatalog().initialRolePermissionSeedCatalog();

    @Test
    void containsExactlyTheNineApprovedBusinessPermissions() {
        assertThat(catalog.permissions()).extracting(PermissionSeed::key).containsExactlyInAnyOrder(
                "CATALOG_READ", "CATALOG_WRITE", "INVENTORY_READ", "INVENTORY_MOVE", "INVENTORY_ADJUST",
                "USER_READ", "USER_WRITE", "ACCESS_READ", "ACCESS_WRITE");
        // Codes start equal to the keys and are therefore unique as well
        assertThat(catalog.permissions()).allMatch(permission -> permission.code().equals(permission.key()));
    }

    @Test
    void containsOnlyTheAdminAndViewerRoles() {
        Map<String, Set<String>> roles = catalog.roles().stream()
                .collect(Collectors.toMap(RoleSeed::key, RoleSeed::permissionKeys));

        assertThat(roles).containsOnlyKeys("ADMIN", "VIEWER");
        assertThat(roles.get("ADMIN")).containsExactlyInAnyOrderElementsOf(
                InitialRolePermissionCatalog.BUSINESS_PERMISSIONS);
        assertThat(roles.get("VIEWER")).containsExactlyInAnyOrder("CATALOG_READ", "INVENTORY_READ");
    }

    @Test
    void writePermissionsForUsersAndAccessBelongOnlyToAdmin() {
        assertThat(catalog.roles())
                .filteredOn(role -> role.permissionKeys().contains("USER_WRITE")
                        || role.permissionKeys().contains("ACCESS_WRITE"))
                .extracting(RoleSeed::key)
                .containsExactly("ADMIN");
    }
}
