package com.solgases.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.solgases.application.exception.DuplicateCategoryNameException;
import com.solgases.domain.model.Category;
import com.solgases.infrastructure.persistence.repository.CategoryJpaRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CategoryPersistenceAdapterTest {

    @Mock
    private CategoryJpaRepository categoryRepository;

    @InjectMocks
    private CategoryPersistenceAdapter adapter;

    @Test
    void saveNewTranslatesDatabaseUniqueViolationToDuplicateCategory() {
        when(categoryRepository.saveAndFlush(any(com.solgases.infrastructure.persistence.entity.CategoryJpaEntity.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        assertThatThrownBy(() -> adapter.saveNew("EPP"))
                .isInstanceOf(DuplicateCategoryNameException.class)
                .hasMessage("A category with the name 'EPP' already exists");
    }

    @Test
    void saveChangesTranslatesDatabaseUniqueViolationToDuplicateCategory() {
        com.solgases.infrastructure.persistence.entity.CategoryJpaEntity entity =
                new com.solgases.infrastructure.persistence.entity.CategoryJpaEntity("EPP");
        ReflectionTestUtils.setField(entity, "id", 1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(categoryRepository.saveAndFlush(entity)).thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        assertThatThrownBy(() -> adapter.saveChanges(new Category(1L, "Gases", true)))
                .isInstanceOf(DuplicateCategoryNameException.class)
                .hasMessage("A category with the name 'Gases' already exists");
    }

    @Test
    void findByIdMapsPersistenceEntityToDomainModel() {
        com.solgases.infrastructure.persistence.entity.CategoryJpaEntity entity =
                new com.solgases.infrastructure.persistence.entity.CategoryJpaEntity("EPP");
        ReflectionTestUtils.setField(entity, "id", 1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(entity));

        assertThat(adapter.findById(1L)).contains(new Category(1L, "EPP", true));
    }
}
