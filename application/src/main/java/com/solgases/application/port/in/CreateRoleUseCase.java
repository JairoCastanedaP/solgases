package com.solgases.application.port.in;

import com.solgases.application.dto.RoleCreateCommand;
import com.solgases.application.dto.RoleResult;

public interface CreateRoleUseCase {

    RoleResult execute(RoleCreateCommand command);
}
