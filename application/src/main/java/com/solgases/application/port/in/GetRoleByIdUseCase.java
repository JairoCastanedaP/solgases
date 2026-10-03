package com.solgases.application.port.in;

import com.solgases.application.dto.RoleResult;

public interface GetRoleByIdUseCase {

    RoleResult execute(Long id);
}
