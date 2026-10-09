package com.solgases.application.port.in;

import com.solgases.application.dto.CredentialProvisioningCommand;

public interface ProvisionUserCredentialUseCase {

    /**
     * Stores the first credential of an existing user and returns its user id. An existing credential is never
     * overwritten.
     */
    Long execute(CredentialProvisioningCommand command);
}
