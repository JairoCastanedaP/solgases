package com.solgases.application.port.in;

import com.solgases.application.dto.UserResult;
import java.util.List;

public interface ListUsersUseCase {

    List<UserResult> execute();
}
