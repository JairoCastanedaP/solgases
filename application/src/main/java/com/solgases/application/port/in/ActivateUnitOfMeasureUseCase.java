package com.solgases.application.port.in;

import com.solgases.application.dto.UnitOfMeasureResult;

public interface ActivateUnitOfMeasureUseCase {
    UnitOfMeasureResult execute(Long id);
}
