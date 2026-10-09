package com.solgases.application.port.in;

import com.solgases.application.dto.FirstAdministratorCommand;

public interface CreateFirstAdministratorUseCase {

    /**
     * Creates the first administrator with its credential and returns its user id. Fails when an active
     * administrator already exists.
     */
    Long execute(FirstAdministratorCommand command);
}
