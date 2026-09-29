package com.solgases.infrastructure.api.rest;

import com.solgases.application.dto.UnitOfMeasureCommand;
import com.solgases.application.dto.UnitOfMeasureResult;
import com.solgases.infrastructure.api.dto.UnitOfMeasureRequest;
import com.solgases.infrastructure.api.dto.UnitOfMeasureResponse;

public final class UnitOfMeasureApiMapper {
    private UnitOfMeasureApiMapper() {}
    public static UnitOfMeasureCommand toCommand(UnitOfMeasureRequest request) {
        return new UnitOfMeasureCommand(request.code(), request.name());
    }
    public static UnitOfMeasureResponse toResponse(UnitOfMeasureResult result) {
        return new UnitOfMeasureResponse(result.id(), result.code(), result.name(), result.active());
    }
}
