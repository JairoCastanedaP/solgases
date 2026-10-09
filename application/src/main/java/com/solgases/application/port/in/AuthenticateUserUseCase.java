package com.solgases.application.port.in;

import com.solgases.application.dto.AuthenticatedUser;
import com.solgases.application.dto.AuthenticationCommand;

public interface AuthenticateUserUseCase {

    /** Verifies the credentials of an active user, failing with AuthenticationFailedException otherwise. */
    AuthenticatedUser execute(AuthenticationCommand command);
}
