package com.solgases.application.usecase;

import com.solgases.application.dto.UnitOfMeasureCommand;
import com.solgases.application.dto.UnitOfMeasureResult;
import com.solgases.application.exception.DuplicateUnitOfMeasureCodeException;
import com.solgases.application.exception.UnitOfMeasureNotFoundException;
import com.solgases.application.port.in.UpdateUnitOfMeasureUseCase;
import com.solgases.application.port.out.UnitOfMeasurePersistencePort;
import org.springframework.transaction.annotation.Transactional;

public class UpdateUnitOfMeasureService implements UpdateUnitOfMeasureUseCase {
    private final UnitOfMeasurePersistencePort persistence;
    public UpdateUnitOfMeasureService(UnitOfMeasurePersistencePort persistence) { this.persistence = persistence; }
    @Override @Transactional
    public UnitOfMeasureResult execute(Long id, UnitOfMeasureCommand command) {
        var unit = persistence.findById(id).orElseThrow(() -> new UnitOfMeasureNotFoundException(id));
        if (persistence.existsByCodeAndIdNot(command.code(), id)) throw new DuplicateUnitOfMeasureCodeException(command.code());
        return UnitOfMeasureResult.from(persistence.saveChanges(unit.update(command.code(), command.name())));
    }
}
