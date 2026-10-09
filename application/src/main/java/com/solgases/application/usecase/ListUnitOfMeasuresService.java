package com.solgases.application.usecase;

import com.solgases.application.dto.UnitOfMeasureResult;
import com.solgases.application.port.in.ListUnitOfMeasuresUseCase;
import com.solgases.application.port.out.UnitOfMeasurePersistencePort;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public class ListUnitOfMeasuresService implements ListUnitOfMeasuresUseCase {
    private final UnitOfMeasurePersistencePort persistence;
    public ListUnitOfMeasuresService(UnitOfMeasurePersistencePort persistence) { this.persistence = persistence; }
    @Override @Transactional(readOnly = true)
    public List<UnitOfMeasureResult> execute() {
        return persistence.findAll().stream().map(UnitOfMeasureResult::from).toList();
    }
}
