package com.solgases.application.port.in;

import com.solgases.application.dto.UserResult;

public interface ActivateUserUseCase {

    UserResult execute(Long id);
}
