package com.solgases.domain.model;

public record Category(Long id, String name, boolean active) {
    public static final int NAME_MAX_LENGTH = 100;

    public Category withName(String newName) {
        return new Category(id, newName, active);
    }

    public Category activate() {
        return new Category(id, name, true);
    }

    public Category deactivate() {
        return new Category(id, name, false);
    }
}
