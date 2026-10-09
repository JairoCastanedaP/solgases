package com.solgases.application.port.in;

import com.solgases.application.dto.UnitOfMeasureCommand;
import com.solgases.application.dto.UnitOfMeasureResult;

public interface CreateUnitOfMeasureUseCase {
    UnitOfMeasureResult execute(UnitOfMeasureCommand command);
}
