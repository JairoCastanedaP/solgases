package com.solgases.application.port.in;

import com.solgases.application.dto.PermissionResult;

public interface GetPermissionByIdUseCase {

    PermissionResult execute(Long id);
}
