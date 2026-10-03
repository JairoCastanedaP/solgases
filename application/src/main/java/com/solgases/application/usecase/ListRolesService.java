package com.solgases.application.usecase;

import com.solgases.application.dto.RoleResult;
import com.solgases.application.port.in.ListRolesUseCase;
import com.solgases.application.port.out.RolePersistencePort;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public class ListRolesService implements ListRolesUseCase {

    private final RolePersistencePort rolePersistencePort;

    public ListRolesService(RolePersistencePort rolePersistencePort) {
        this.rolePersistencePort = rolePersistencePort;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResult> execute() {
        return rolePersistencePort.findAll().stream().map(RoleResult::from).toList();
    }
}
