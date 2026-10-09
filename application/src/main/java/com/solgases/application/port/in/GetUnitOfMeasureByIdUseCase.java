package com.solgases.application.port.in;

import com.solgases.application.dto.UnitOfMeasureResult;

public interface GetUnitOfMeasureByIdUseCase {
    UnitOfMeasureResult execute(Long id);
}
