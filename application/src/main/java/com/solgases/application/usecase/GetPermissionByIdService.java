package com.solgases.application.usecase;

import com.solgases.application.dto.PermissionResult;
import com.solgases.application.exception.PermissionNotFoundException;
import com.solgases.application.port.in.GetPermissionByIdUseCase;
import com.solgases.application.port.out.PermissionPersistencePort;
import org.springframework.transaction.annotation.Transactional;

public class GetPermissionByIdService implements GetPermissionByIdUseCase {

    private final PermissionPersistencePort permissionPersistencePort;

    public GetPermissionByIdService(PermissionPersistencePort permissionPersistencePort) {
        this.permissionPersistencePort = permissionPersistencePort;
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResult execute(Long id) {
        return PermissionResult.from(permissionPersistencePort.findById(id)
                .orElseThrow(() -> new PermissionNotFoundException(id)));
    }
}
