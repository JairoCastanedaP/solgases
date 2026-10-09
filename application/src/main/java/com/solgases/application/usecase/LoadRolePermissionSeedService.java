package com.solgases.application.usecase;

import com.solgases.application.dto.PermissionSeed;
import com.solgases.application.dto.RolePermissionSeedCatalog;
import com.solgases.application.dto.RolePermissionSeedResult;
import com.solgases.application.dto.RoleSeed;
import com.solgases.application.port.in.LoadRolePermissionSeedUseCase;
import com.solgases.application.port.out.PermissionPersistencePort;
import com.solgases.application.port.out.RolePersistencePort;
import com.solgases.domain.model.Permission;
import com.solgases.domain.model.Role;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;

/**
 * Loads the initial roles and permissions idempotently, identifying each one by its internal key.
 * Existing records are never modified: neither their editable values nor, for roles, their permissions.
 * A role receives its seed permissions only when it is created for the first time.
 */
public class LoadRolePermissionSeedService implements LoadRolePermissionSeedUseCase {

    private final RolePersistencePort rolePersistencePort;
    private final PermissionPersistencePort permissionPersistencePort;

    public LoadRolePermissionSeedService(RolePersistencePort rolePersistencePort,
            PermissionPersistencePort permissionPersistencePort) {
        this.rolePersistencePort = rolePersistencePort;
        this.permissionPersistencePort = permissionPersistencePort;
    }

    @Override
    @Transactional
    public RolePermissionSeedResult execute(RolePermissionSeedCatalog catalog) {
        int createdPermissions = 0;
        for (PermissionSeed seed : catalog.permissions()) {
            if (!permissionPersistencePort.existsByKey(seed.key())) {
                permissionPersistencePort.saveNew(Permission.newPermission(seed.key(), seed.code()));
                createdPermissions++;
            }
        }
        int createdRoles = 0;
        for (RoleSeed seed : catalog.roles()) {
            if (!rolePersistencePort.existsByKey(seed.key())) {
                rolePersistencePort.saveNew(Role.newRole(seed.key(), seed.name(), resolveSeedPermissions(seed)));
                createdRoles++;
            }
        }
        return new RolePermissionSeedResult(createdPermissions, createdRoles);
    }

    private Set<Permission> resolveSeedPermissions(RoleSeed seed) {
        if (seed.permissionKeys().isEmpty()) {
            return Set.of();
        }
        List<Permission> permissions = permissionPersistencePort.findAllByKeys(seed.permissionKeys());
        Set<String> missing = new HashSet<>(seed.permissionKeys());
        permissions.forEach(permission -> missing.remove(permission.key()));
        if (!missing.isEmpty()) {
            throw new IllegalStateException(
                    "Seed role '" + seed.key() + "' references unknown permission keys " + missing);
        }
        return Set.copyOf(permissions);
    }
}
