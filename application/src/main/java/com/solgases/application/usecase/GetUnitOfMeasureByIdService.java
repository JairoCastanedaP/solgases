package com.solgases.application.usecase;

import com.solgases.application.dto.UnitOfMeasureResult;
import com.solgases.application.exception.UnitOfMeasureNotFoundException;
import com.solgases.application.port.in.GetUnitOfMeasureByIdUseCase;
import com.solgases.application.port.out.UnitOfMeasurePersistencePort;
import org.springframework.transaction.annotation.Transactional;

public class GetUnitOfMeasureByIdService implements GetUnitOfMeasureByIdUseCase {
    private final UnitOfMeasurePersistencePort persistence;
    public GetUnitOfMeasureByIdService(UnitOfMeasurePersistencePort persistence) { this.persistence = persistence; }
    @Override @Transactional(readOnly = true)
    public UnitOfMeasureResult execute(Long id) {
        return persistence.findById(id).map(UnitOfMeasureResult::from)
                .orElseThrow(() -> new UnitOfMeasureNotFoundException(id));
    }
}
