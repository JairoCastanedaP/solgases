package com.solgases.application.port.in;

import com.solgases.application.dto.UserResult;

public interface GetUserByIdUseCase {

    UserResult execute(Long id);
}
