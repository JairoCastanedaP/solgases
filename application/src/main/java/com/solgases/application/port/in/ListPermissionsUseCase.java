package com.solgases.application.port.in;

import com.solgases.application.dto.PermissionResult;
import java.util.List;

public interface ListPermissionsUseCase {

    List<PermissionResult> execute();
}
