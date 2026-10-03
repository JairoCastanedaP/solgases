package com.solgases.application.usecase;

import com.solgases.application.exception.PermissionNotFoundException;
import com.solgases.application.exception.RoleNotFoundException;
import com.solgases.application.port.out.PermissionPersistencePort;
import com.solgases.application.port.out.RolePersistencePort;
import com.solgases.domain.model.Permission;
import com.solgases.domain.model.Role;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Resolves role and permission ids sent by clients, failing with 404 when any of them does not exist. */
final class AccessReferenceResolver {

    private AccessReferenceResolver() {
    }

    static Set<Role> resolveRoles(RolePersistencePort rolePersistencePort, Set<Long> roleIds) {
        if (roleIds.isEmpty()) {
            return Set.of();
        }
        Set<Role> roles = Set.copyOf(rolePersistencePort.findAllByIds(roleIds));
        List<Long> missing = missingIds(roleIds, roles.stream().map(Role::id).toList());
        if (!missing.isEmpty()) {
            throw new RoleNotFoundException(missing);
        }
        return roles;
    }

    static Set<Permission> resolvePermissions(PermissionPersistencePort permissionPersistencePort,
            Set<Long> permissionIds) {
        if (permissionIds.isEmpty()) {
            return Set.of();
        }
        Set<Permission> permissions = Set.copyOf(permissionPersistencePort.findAllByIds(permissionIds));
        List<Long> missing = missingIds(permissionIds, permissions.stream().map(Permission::id).toList());
        if (!missing.isEmpty()) {
            throw new PermissionNotFoundException(missing);
        }
        return permissions;
    }

    private static List<Long> missingIds(Set<Long> requested, List<Long> found) {
        Set<Long> missing = new HashSet<>(requested);
        found.forEach(missing::remove);
        return missing.stream().sorted().toList();
    }
}
