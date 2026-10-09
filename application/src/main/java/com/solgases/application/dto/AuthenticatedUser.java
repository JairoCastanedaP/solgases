package com.solgases.application.dto;

/** Result of a successful authentication: the internal id of the active user. */
public record AuthenticatedUser(Long userId) {
}
