package com.solgases.application.port.in;

import com.solgases.application.dto.RoleResult;
import java.util.List;

public interface ListRolesUseCase {

    List<RoleResult> execute();
}
