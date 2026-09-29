package com.solgases.application.dto;

import com.solgases.domain.model.UnitOfMeasure;

public record UnitOfMeasureResult(Long id, String code, String name, boolean active) {

    public static UnitOfMeasureResult from(UnitOfMeasure unit) {
        return new UnitOfMeasureResult(unit.id(), unit.code(), unit.name(), unit.active());
    }
}
