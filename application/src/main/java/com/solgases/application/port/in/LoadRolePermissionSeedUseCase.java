package com.solgases.application.port.in;

import com.solgases.application.dto.RolePermissionSeedCatalog;
import com.solgases.application.dto.RolePermissionSeedResult;

public interface LoadRolePermissionSeedUseCase {

    RolePermissionSeedResult execute(RolePermissionSeedCatalog catalog);
}
