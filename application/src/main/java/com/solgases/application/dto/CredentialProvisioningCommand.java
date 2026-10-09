package com.solgases.application.dto;

/**
 * First password of an existing user. The password is kept as characters so that the caller can clear it after
 * use, and it is never included in {@link #toString()}.
 */
public record CredentialProvisioningCommand(String username, char[] password) {

    public CredentialProvisioningCommand {
        PasswordPolicy.requireValidNewPassword(password);
    }

    @Override
    public String toString() {
        return "CredentialProvisioningCommand[username=" + username + ", password=****]";
    }
}
