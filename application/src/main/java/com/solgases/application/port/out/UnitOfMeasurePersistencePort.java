package com.solgases.application.port.out;

import com.solgases.domain.model.UnitOfMeasure;
import java.util.List;
import java.util.Optional;

public interface UnitOfMeasurePersistencePort {

    Optional<UnitOfMeasure> findById(Long id);

    List<UnitOfMeasure> findAll();

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    UnitOfMeasure saveNew(String code, String name);

    UnitOfMeasure saveChanges(UnitOfMeasure unit);
}
