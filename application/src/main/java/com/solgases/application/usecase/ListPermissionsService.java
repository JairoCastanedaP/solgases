package com.solgases.application.usecase;

import com.solgases.application.dto.PermissionResult;
import com.solgases.application.port.in.ListPermissionsUseCase;
import com.solgases.application.port.out.PermissionPersistencePort;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public class ListPermissionsService implements ListPermissionsUseCase {

    private final PermissionPersistencePort permissionPersistencePort;

    public ListPermissionsService(PermissionPersistencePort permissionPersistencePort) {
        this.permissionPersistencePort = permissionPersistencePort;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResult> execute() {
        return permissionPersistencePort.findAll().stream().map(PermissionResult::from).toList();
    }
}
