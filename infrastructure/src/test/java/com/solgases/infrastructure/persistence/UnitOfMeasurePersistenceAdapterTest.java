package com.solgases.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.solgases.application.exception.DuplicateUnitOfMeasureCodeException;
import com.solgases.infrastructure.persistence.repository.UnitOfMeasureJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class UnitOfMeasurePersistenceAdapterTest {

    @Mock
    private UnitOfMeasureJpaRepository repository;

    @InjectMocks
    private UnitOfMeasurePersistenceAdapter adapter;

    @Test
    void saveNewTranslatesUniqueConstraintViolation() {
        when(repository.saveAndFlush(any(com.solgases.infrastructure.persistence.entity.UnitOfMeasureJpaEntity.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        assertThatThrownBy(() -> adapter.saveNew("UN", "Unidad"))
                .isInstanceOf(DuplicateUnitOfMeasureCodeException.class)
                .hasMessage("A unit of measure with the code 'UN' already exists");
    }
}
