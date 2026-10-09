package com.solgases.infrastructure.config;

import com.solgases.application.port.out.PermissionPersistencePort;
import com.solgases.application.port.out.RolePersistencePort;
import com.solgases.application.usecase.CreatePermissionService;
import com.solgases.application.usecase.CreateRoleService;
import com.solgases.application.usecase.GetPermissionByIdService;
import com.solgases.application.usecase.GetRoleByIdService;
import com.solgases.application.usecase.ListPermissionsService;
import com.solgases.application.usecase.ListRolesService;
import com.solgases.application.usecase.LoadRolePermissionSeedService;
import com.solgases.application.usecase.UpdatePermissionService;
import com.solgases.application.usecase.UpdateRoleService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RolePermissionUseCaseConfiguration {

    @Bean
    CreateRoleService createRoleService(RolePersistencePort rolePersistencePort,
            PermissionPersistencePort permissionPersistencePort) {
        return new CreateRoleService(rolePersistencePort, permissionPersistencePort);
    }

    @Bean
    ListRolesService listRolesService(RolePersistencePort rolePersistencePort) {
        return new ListRolesService(rolePersistencePort);
    }

    @Bean
    GetRoleByIdService getRoleByIdService(RolePersistencePort rolePersistencePort) {
        return new GetRoleByIdService(rolePersistencePort);
    }

    @Bean
    UpdateRoleService updateRoleService(RolePersistencePort rolePersistencePort,
            PermissionPersistencePort permissionPersistencePort) {
        return new UpdateRoleService(rolePersistencePort, permissionPersistencePort);
    }

    @Bean
    CreatePermissionService createPermissionService(PermissionPersistencePort permissionPersistencePort) {
        return new CreatePermissionService(permissionPersistencePort);
    }

    @Bean
    ListPermissionsService listPermissionsService(PermissionPersistencePort permissionPersistencePort) {
        return new ListPermissionsService(permissionPersistencePort);
    }

    @Bean
    GetPermissionByIdService getPermissionByIdService(PermissionPersistencePort permissionPersistencePort) {
        return new GetPermissionByIdService(permissionPersistencePort);
    }

    @Bean
    UpdatePermissionService updatePermissionService(PermissionPersistencePort permissionPersistencePort) {
        return new UpdatePermissionService(permissionPersistencePort);
    }

    @Bean
    LoadRolePermissionSeedService loadRolePermissionSeedService(RolePersistencePort rolePersistencePort,
            PermissionPersistencePort permissionPersistencePort) {
        return new LoadRolePermissionSeedService(rolePersistencePort, permissionPersistencePort);
    }
}
