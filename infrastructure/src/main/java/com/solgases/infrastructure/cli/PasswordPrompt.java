package com.solgases.infrastructure.cli;

/** Reads a password without echoing it. The caller clears the returned characters after use. */
public interface PasswordPrompt {

    char[] readPassword(String prompt);
}
