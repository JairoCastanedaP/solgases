package com.solgases.application.port.in;

import com.solgases.application.dto.UnitOfMeasureCommand;
import com.solgases.application.dto.UnitOfMeasureResult;

public interface UpdateUnitOfMeasureUseCase {
    UnitOfMeasureResult execute(Long id, UnitOfMeasureCommand command);
}
