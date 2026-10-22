package com.solgases.application.dto;

import java.util.Arrays;
import java.util.Objects;

/**
 * Data of the first administrator. The password is kept as characters so that the caller can clear it after
 * use, and it is never included in {@link #toString()}. Equality compares the password by content.
 */
public record FirstAdministratorCommand(String username, String displayName, char[] password) {

    public FirstAdministratorCommand {
        PasswordPolicy.requireValidNewPassword(password);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof FirstAdministratorCommand(String otherUsername, String otherDisplayName,
                char[] otherPassword)
                && Objects.equals(username, otherUsername)
                && Objects.equals(displayName, otherDisplayName)
                && Arrays.equals(password, otherPassword);
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hash(username, displayName) + Arrays.hashCode(password);
    }

    @Override
    public String toString() {
        return "FirstAdministratorCommand[username=" + username + ", displayName=" + displayName + ", password=****]";
    }
}
