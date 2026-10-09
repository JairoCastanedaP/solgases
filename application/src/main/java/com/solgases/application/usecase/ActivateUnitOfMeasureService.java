package com.solgases.application.usecase;

import com.solgases.application.dto.UnitOfMeasureResult;
import com.solgases.application.exception.UnitOfMeasureNotFoundException;
import com.solgases.application.port.in.ActivateUnitOfMeasureUseCase;
import com.solgases.application.port.out.UnitOfMeasurePersistencePort;
import org.springframework.transaction.annotation.Transactional;

public class ActivateUnitOfMeasureService implements ActivateUnitOfMeasureUseCase {
    private final UnitOfMeasurePersistencePort persistence;
    public ActivateUnitOfMeasureService(UnitOfMeasurePersistencePort persistence) { this.persistence = persistence; }
    @Override @Transactional
    public UnitOfMeasureResult execute(Long id) {
        var unit = persistence.findById(id).orElseThrow(() -> new UnitOfMeasureNotFoundException(id));
        return UnitOfMeasureResult.from(persistence.saveChanges(unit.activate()));
    }
}
