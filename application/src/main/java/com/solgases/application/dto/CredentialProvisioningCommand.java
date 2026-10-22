package com.solgases.application.dto;

import java.util.Arrays;
import java.util.Objects;

/**
 * First password of an existing user. The password is kept as characters so that the caller can clear it after
 * use, and it is never included in {@link #toString()}. Equality compares the password by content.
 */
public record CredentialProvisioningCommand(String username, char[] password) {

    public CredentialProvisioningCommand {
        PasswordPolicy.requireValidNewPassword(password);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof CredentialProvisioningCommand(String otherUsername, char[] otherPassword)
                && Objects.equals(username, otherUsername)
                && Arrays.equals(password, otherPassword);
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hashCode(username) + Arrays.hashCode(password);
    }

    @Override
    public String toString() {
        return "CredentialProvisioningCommand[username=" + username + ", password=****]";
    }
}
