package com.solgases.application.port.in;

import com.solgases.application.dto.PermissionCreateCommand;
import com.solgases.application.dto.PermissionResult;

public interface CreatePermissionUseCase {

    PermissionResult execute(PermissionCreateCommand command);
}
