package com.solgases.application.port.in;

import com.solgases.application.dto.RoleResult;
import com.solgases.application.dto.RoleUpdateCommand;

public interface UpdateRoleUseCase {

    RoleResult execute(Long id, RoleUpdateCommand command);
}
