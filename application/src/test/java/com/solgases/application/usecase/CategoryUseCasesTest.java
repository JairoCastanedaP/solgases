package com.solgases.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.solgases.application.dto.CategoryCommand;
import com.solgases.application.dto.CategoryResult;
import com.solgases.application.exception.CategoryNotFoundException;
import com.solgases.application.exception.DuplicateCategoryNameException;
import com.solgases.application.port.out.CategoryPersistencePort;
import com.solgases.domain.model.Category;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryUseCasesTest {

    @Mock
    private CategoryPersistencePort categoryPersistencePort;

    @InjectMocks
    private CreateCategoryService createCategoryService;

    @InjectMocks
    private ListCategoriesService listCategoriesService;

    @InjectMocks
    private GetCategoryByIdService getCategoryByIdService;

    @InjectMocks
    private UpdateCategoryService updateCategoryService;

    @InjectMocks
    private ActivateCategoryService activateCategoryService;

    @InjectMocks
    private DeactivateCategoryService deactivateCategoryService;

    @Test
    void createPreservesNameAndCreatesActiveCategory() {
        Category category = new Category(7L, " EPP ", true);
        when(categoryPersistencePort.saveNew(" EPP ")).thenReturn(category);

        assertThat(createCategoryService.execute(new CategoryCommand(" EPP ")))
                .isEqualTo(new CategoryResult(7L, " EPP ", true));
        verify(categoryPersistencePort).existsByName(" EPP ");
    }

    @Test
    void createWithDuplicateNameThrowsConflictWithoutSaving() {
        when(categoryPersistencePort.existsByName("EPP")).thenReturn(true);

        assertThatThrownBy(() -> createCategoryService.execute(new CategoryCommand("EPP")))
                .isInstanceOf(DuplicateCategoryNameException.class);
        verify(categoryPersistencePort, never()).saveNew("EPP");
    }

    @Test
    void listReturnsActiveAndInactiveCategories() {
        when(categoryPersistencePort.findAll()).thenReturn(List.of(
                new Category(1L, "EPP", true), new Category(2L, "Extintores", false)));

        assertThat(listCategoriesService.execute()).containsExactly(
                new CategoryResult(1L, "EPP", true), new CategoryResult(2L, "Extintores", false));
    }

    @Test
    void findByIdReturnsExistingCategory() {
        when(categoryPersistencePort.findById(1L)).thenReturn(Optional.of(new Category(1L, "EPP", true)));

        assertThat(getCategoryByIdService.execute(1L)).isEqualTo(new CategoryResult(1L, "EPP", true));
    }

    @Test
    void findByIdThrowsNotFoundWhenMissing() {
        when(categoryPersistencePort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getCategoryByIdService.execute(99L))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    void updateChangesNameAndKeepsInactiveState() {
        Category inactive = new Category(1L, "EPP", false);
        when(categoryPersistencePort.findById(1L)).thenReturn(Optional.of(inactive));
        when(categoryPersistencePort.saveChanges(inactive.withName("Gases")))
                .thenReturn(new Category(1L, "Gases", false));

        assertThat(updateCategoryService.execute(1L, new CategoryCommand("Gases")))
                .isEqualTo(new CategoryResult(1L, "Gases", false));
    }

    @Test
    void updateAllowsKeepingItsOwnName() {
        Category category = new Category(1L, "EPP", true);
        when(categoryPersistencePort.findById(1L)).thenReturn(Optional.of(category));
        when(categoryPersistencePort.saveChanges(category)).thenReturn(category);

        assertThat(updateCategoryService.execute(1L, new CategoryCommand("EPP")).name()).isEqualTo("EPP");
    }

    @Test
    void updateThrowsNotFoundWhenMissing() {
        when(categoryPersistencePort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateCategoryService.execute(99L, new CategoryCommand("EPP")))
                .isInstanceOf(CategoryNotFoundException.class);
        verify(categoryPersistencePort, never()).saveChanges(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void updateWithNameUsedByAnotherCategoryThrowsConflict() {
        when(categoryPersistencePort.findById(1L)).thenReturn(Optional.of(new Category(1L, "EPP", true)));
        when(categoryPersistencePort.existsByNameAndIdNot("Gases", 1L)).thenReturn(true);

        assertThatThrownBy(() -> updateCategoryService.execute(1L, new CategoryCommand("Gases")))
                .isInstanceOf(DuplicateCategoryNameException.class);
        verify(categoryPersistencePort, never()).saveChanges(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void activateMarksInactiveCategoryActiveAndIsIdempotent() {
        Category inactive = new Category(1L, "EPP", false);
        Category active = inactive.activate();
        when(categoryPersistencePort.findById(1L)).thenReturn(Optional.of(inactive));
        when(categoryPersistencePort.saveChanges(active)).thenReturn(active);

        assertThat(activateCategoryService.execute(1L)).isEqualTo(new CategoryResult(1L, "EPP", true));
        assertThat(activateCategoryService.execute(1L)).isEqualTo(new CategoryResult(1L, "EPP", true));
    }

    @Test
    void deactivateMarksCategoryInactiveAndIsIdempotent() {
        Category active = new Category(1L, "EPP", true);
        Category inactive = active.deactivate();
        when(categoryPersistencePort.findById(1L)).thenReturn(Optional.of(active));
        when(categoryPersistencePort.saveChanges(inactive)).thenReturn(inactive);

        assertThat(deactivateCategoryService.execute(1L)).isEqualTo(new CategoryResult(1L, "EPP", false));
        assertThat(deactivateCategoryService.execute(1L)).isEqualTo(new CategoryResult(1L, "EPP", false));
    }

    @Test
    void activateAndDeactivateThrowNotFoundWhenMissing() {
        when(categoryPersistencePort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activateCategoryService.execute(99L)).isInstanceOf(CategoryNotFoundException.class);
        assertThatThrownBy(() -> deactivateCategoryService.execute(99L)).isInstanceOf(CategoryNotFoundException.class);
    }
}
