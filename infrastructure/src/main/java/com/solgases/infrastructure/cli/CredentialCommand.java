package com.solgases.infrastructure.cli;

import java.util.Arrays;

/**
 * Local credential commands. They are requested with {@code --solgases.credentials.command=<value>} and run
 * without a web server: there is no REST operation to create administrators or credentials.
 */
public enum CredentialCommand {

    /** Creates the first administrator; refused when an active administrator already exists. */
    CREATE_FIRST_ADMIN("create-first-admin"),
    /** Stores the first credential of an existing user; an existing credential is never overwritten. */
    PROVISION_CREDENTIAL("provision-credential");

    public static final String PROPERTY = "solgases.credentials.command";

    private final String value;

    CredentialCommand(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static CredentialCommand fromValue(String value) {
        return Arrays.stream(values())
                .filter(command -> command.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown credential command '" + value
                        + "'; expected one of " + Arrays.stream(values()).map(CredentialCommand::value).toList()));
    }

    /** Whether the command line asks for a credential command. */
    public static boolean isRequested(String[] args) {
        return Arrays.stream(args).anyMatch(arg -> arg.startsWith("--" + PROPERTY + "="));
    }
}
