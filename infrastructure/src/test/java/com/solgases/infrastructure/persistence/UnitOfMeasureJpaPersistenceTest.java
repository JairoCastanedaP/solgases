package com.solgases.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.solgases.application.exception.DuplicateUnitOfMeasureCodeException;
import com.solgases.application.exception.UnitOfMeasureNotFoundException;
import com.solgases.domain.model.UnitOfMeasure;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

/** Persistence of units of measure against a real JPA provider and an in-memory H2 database. */
@DataJpaTest(showSql = false)
@Import(UnitOfMeasurePersistenceAdapter.class)
class UnitOfMeasureJpaPersistenceTest {

    @Autowired
    private UnitOfMeasurePersistenceAdapter adapter;

    @Test
    void newUnitIsPersistedActiveAndMappedBack() {
        UnitOfMeasure saved = adapter.saveNew("KG", "Kilogram");

        assertThat(saved.id()).isNotNull();
        assertThat(adapter.findById(saved.id())).contains(new UnitOfMeasure(saved.id(), "KG", "Kilogram", true));
        assertThat(adapter.findAll()).containsExactly(saved);
    }

    @Test
    void changesUpdateCodeNameAndActiveFlag() {
        UnitOfMeasure saved = adapter.saveNew("KG", "Kilogram");

        adapter.saveChanges(new UnitOfMeasure(saved.id(), "LB", "Pound", false));

        assertThat(adapter.findById(saved.id())).contains(new UnitOfMeasure(saved.id(), "LB", "Pound", false));
        assertThat(adapter.existsByCode("KG")).isFalse();
        assertThat(adapter.existsByCodeAndIdNot("LB", saved.id())).isFalse();
    }

    @Test
    void databaseRejectsDuplicateCodes() {
        adapter.saveNew("KG", "Kilogram");

        assertThatThrownBy(() -> adapter.saveNew("KG", "Other"))
                .isInstanceOf(DuplicateUnitOfMeasureCodeException.class);
    }

    @Test
    void changingAMissingUnitFails() {
        assertThatThrownBy(() -> adapter.saveChanges(new UnitOfMeasure(999L, "KG", "Kilogram", true)))
                .isInstanceOf(UnitOfMeasureNotFoundException.class);
    }
}
