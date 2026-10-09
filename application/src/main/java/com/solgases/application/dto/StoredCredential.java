package com.solgases.application.dto;

/** Stored password hash of a user. The hash is never included in {@link #toString()}. */
public record StoredCredential(Long userId, String passwordHash) {

    @Override
    public String toString() {
        return "StoredCredential[userId=" + userId + ", passwordHash=****]";
    }
}
