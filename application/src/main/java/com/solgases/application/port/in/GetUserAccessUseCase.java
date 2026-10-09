package com.solgases.application.port.in;

import com.solgases.application.dto.UserAccess;
import java.util.Optional;

public interface GetUserAccessUseCase {

    /** Current state and permission keys of the user, or empty when the user does not exist. */
    Optional<UserAccess> execute(Long userId);
}
