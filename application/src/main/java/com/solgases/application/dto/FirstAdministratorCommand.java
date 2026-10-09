package com.solgases.application.dto;

/**
 * Data of the first administrator. The password is kept as characters so that the caller can clear it after
 * use, and it is never included in {@link #toString()}.
 */
public record FirstAdministratorCommand(String username, String displayName, char[] password) {

    public FirstAdministratorCommand {
        PasswordPolicy.requireValidNewPassword(password);
    }

    @Override
    public String toString() {
        return "FirstAdministratorCommand[username=" + username + ", displayName=" + displayName + ", password=****]";
    }
}
