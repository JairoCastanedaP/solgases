package com.solgases.domain.model;

import java.time.Instant;

/**
 * A permission. The key is a stable internal identifier that never changes once created;
 * the code is editable.
 */
public record Permission(Long id, String key, String code, Instant createdAt, Instant updatedAt) {

    public static final int KEY_MAX_LENGTH = 50;
    public static final int CODE_MAX_LENGTH = 100;

    public static Permission newPermission(String key, String code) {
        return new Permission(null, key, code, null, null);
    }

    public Permission withCode(String newCode) {
        return new Permission(id, key, newCode, createdAt, updatedAt);
    }
}
