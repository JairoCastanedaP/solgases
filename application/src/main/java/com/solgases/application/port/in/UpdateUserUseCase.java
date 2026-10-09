package com.solgases.application.port.in;

import com.solgases.application.dto.UserCommand;
import com.solgases.application.dto.UserResult;

public interface UpdateUserUseCase {

    UserResult execute(Long id, UserCommand command);
}
