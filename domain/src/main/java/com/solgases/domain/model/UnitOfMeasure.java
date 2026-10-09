package com.solgases.domain.model;

public record UnitOfMeasure(Long id, String code, String name, boolean active) {

    public static final int CODE_MAX_LENGTH = 20;
    public static final int NAME_MAX_LENGTH = 100;

    public UnitOfMeasure update(String newCode, String newName) {
        return new UnitOfMeasure(id, newCode, newName, active);
    }

    public UnitOfMeasure activate() {
        return new UnitOfMeasure(id, code, name, true);
    }

    public UnitOfMeasure deactivate() {
        return new UnitOfMeasure(id, code, name, false);
    }
}
