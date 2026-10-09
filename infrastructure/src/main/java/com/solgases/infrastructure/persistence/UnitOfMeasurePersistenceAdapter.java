package com.solgases.infrastructure.persistence;

import com.solgases.application.exception.DuplicateUnitOfMeasureCodeException;
import com.solgases.application.exception.UnitOfMeasureNotFoundException;
import com.solgases.application.port.out.UnitOfMeasurePersistencePort;
import com.solgases.infrastructure.persistence.repository.UnitOfMeasureJpaRepository;
import com.solgases.infrastructure.persistence.mapper.UnitOfMeasurePersistenceMapper;
import com.solgases.domain.model.UnitOfMeasure;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
public class UnitOfMeasurePersistenceAdapter implements UnitOfMeasurePersistencePort {

    private final UnitOfMeasureJpaRepository repository;

    public UnitOfMeasurePersistenceAdapter(UnitOfMeasureJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<UnitOfMeasure> findById(Long id) {
        return repository.findById(id).map(UnitOfMeasurePersistenceMapper::toDomain);
    }

    @Override
    public List<UnitOfMeasure> findAll() {
        return repository.findAll().stream().map(UnitOfMeasurePersistenceMapper::toDomain).toList();
    }

    @Override
    public boolean existsByCode(String code) { return repository.existsByCode(code); }

    @Override
    public boolean existsByCodeAndIdNot(String code, Long id) { return repository.existsByCodeAndIdNot(code, id); }

    @Override
    public UnitOfMeasure saveNew(String code, String name) {
        try {
            return UnitOfMeasurePersistenceMapper.toDomain(repository.saveAndFlush(
                    UnitOfMeasurePersistenceMapper.toNewEntity(code, name)));
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateUnitOfMeasureCodeException(code);
        }
    }

    @Override
    public UnitOfMeasure saveChanges(UnitOfMeasure unit) {
        var entity = repository.findById(unit.id()).orElseThrow(() -> new UnitOfMeasureNotFoundException(unit.id()));
        entity.update(unit.code(), unit.name());
        if (unit.active()) entity.activate(); else entity.deactivate();
        try {
            return UnitOfMeasurePersistenceMapper.toDomain(repository.saveAndFlush(entity));
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateUnitOfMeasureCodeException(unit.code());
        }
    }

}
