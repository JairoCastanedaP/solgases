package com.solgases.application.port.in;

import com.solgases.application.dto.UnitOfMeasureResult;

public interface DeactivateUnitOfMeasureUseCase {
    UnitOfMeasureResult execute(Long id);
}
