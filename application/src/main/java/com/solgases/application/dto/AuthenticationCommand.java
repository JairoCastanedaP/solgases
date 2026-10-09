package com.solgases.application.dto;

/** Credentials sent by a client to authenticate. The password is never included in {@link #toString()}. */
public record AuthenticationCommand(String username, String password) {

    @Override
    public String toString() {
        return "AuthenticationCommand[username=" + username + ", password=****]";
    }
}
