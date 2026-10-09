package com.solgases.application.port.in;

import com.solgases.application.dto.UserResult;

public interface DeactivateUserUseCase {

    UserResult execute(Long id);
}
