package com.solgases.application.port.in;

import com.solgases.application.dto.PermissionResult;
import com.solgases.application.dto.PermissionUpdateCommand;

public interface UpdatePermissionUseCase {

    PermissionResult execute(Long id, PermissionUpdateCommand command);
}
