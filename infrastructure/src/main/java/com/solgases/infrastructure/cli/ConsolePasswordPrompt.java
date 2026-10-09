package com.solgases.infrastructure.cli;

import java.io.Console;

/**
 * Reads the password from the interactive terminal without echoing it. Passwords are never accepted as command
 * line arguments, system properties or environment variables, so they cannot end up in the shell history,
 * the process list or the logs.
 */
public class ConsolePasswordPrompt implements PasswordPrompt {

    @Override
    public char[] readPassword(String prompt) {
        Console console = System.console();
        if (console == null || !console.isTerminal()) {
            throw new IllegalStateException("Credential commands need an interactive terminal to read the password");
        }
        char[] password = console.readPassword("%s", prompt);
        return password == null ? new char[0] : password;
    }
}
