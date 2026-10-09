package com.solgases.application.usecase;

import com.solgases.application.dto.UnitOfMeasureResult;
import com.solgases.application.exception.UnitOfMeasureNotFoundException;
import com.solgases.application.port.in.DeactivateUnitOfMeasureUseCase;
import com.solgases.application.port.out.UnitOfMeasurePersistencePort;
import org.springframework.transaction.annotation.Transactional;

public class DeactivateUnitOfMeasureService implements DeactivateUnitOfMeasureUseCase {
    private final UnitOfMeasurePersistencePort persistence;
    public DeactivateUnitOfMeasureService(UnitOfMeasurePersistencePort persistence) { this.persistence = persistence; }
    @Override @Transactional
    public UnitOfMeasureResult execute(Long id) {
        var unit = persistence.findById(id).orElseThrow(() -> new UnitOfMeasureNotFoundException(id));
        return UnitOfMeasureResult.from(persistence.saveChanges(unit.deactivate()));
    }
}
