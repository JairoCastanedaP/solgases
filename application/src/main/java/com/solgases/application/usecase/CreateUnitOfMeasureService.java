package com.solgases.application.usecase;

import com.solgases.application.dto.UnitOfMeasureCommand;
import com.solgases.application.dto.UnitOfMeasureResult;
import com.solgases.application.exception.DuplicateUnitOfMeasureCodeException;
import com.solgases.application.port.in.CreateUnitOfMeasureUseCase;
import com.solgases.application.port.out.UnitOfMeasurePersistencePort;
import org.springframework.transaction.annotation.Transactional;

public class CreateUnitOfMeasureService implements CreateUnitOfMeasureUseCase {
    private final UnitOfMeasurePersistencePort persistence;
    public CreateUnitOfMeasureService(UnitOfMeasurePersistencePort persistence) { this.persistence = persistence; }
    @Override @Transactional
    public UnitOfMeasureResult execute(UnitOfMeasureCommand command) {
        if (persistence.existsByCode(command.code())) throw new DuplicateUnitOfMeasureCodeException(command.code());
        return UnitOfMeasureResult.from(persistence.saveNew(command.code(), command.name()));
    }
}
