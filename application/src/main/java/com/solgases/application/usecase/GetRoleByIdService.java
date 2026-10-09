package com.solgases.application.usecase;

import com.solgases.application.dto.RoleResult;
import com.solgases.application.exception.RoleNotFoundException;
import com.solgases.application.port.in.GetRoleByIdUseCase;
import com.solgases.application.port.out.RolePersistencePort;
import org.springframework.transaction.annotation.Transactional;

public class GetRoleByIdService implements GetRoleByIdUseCase {

    private final RolePersistencePort rolePersistencePort;

    public GetRoleByIdService(RolePersistencePort rolePersistencePort) {
        this.rolePersistencePort = rolePersistencePort;
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResult execute(Long id) {
        return RoleResult.from(rolePersistencePort.findById(id).orElseThrow(() -> new RoleNotFoundException(id)));
    }
}
