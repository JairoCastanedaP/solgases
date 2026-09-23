package com.solgases.category.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.solgases.category.dto.CategoryRequest;
import com.solgases.category.dto.CategoryResponse;
import com.solgases.category.entity.Category;
import com.solgases.category.repository.CategoryRepository;
import com.solgases.exception.ResourceConflictException;
import com.solgases.exception.ResourceNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private static Category category(Long id, String name, boolean active) {
        Category category = new Category(name);
        ReflectionTestUtils.setField(category, "id", id);
        if (!active) {
            category.deactivate();
        }
        return category;
    }

    // create

    @Test
    void createSavesNewActiveCategoryWithNameAsReceived() {
        when(categoryRepository.existsByName(" EPP ")).thenReturn(false);
        when(categoryRepository.saveAndFlush(any(Category.class))).thenAnswer(invocation -> {
            Category toSave = invocation.getArgument(0);
            ReflectionTestUtils.setField(toSave, "id", 7L);
            return toSave;
        });

        CategoryResponse response = categoryService.create(new CategoryRequest(" EPP "));

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo(" EPP ");
        assertThat(captor.getValue().isActive()).isTrue();
        assertThat(response).isEqualTo(new CategoryResponse(7L, " EPP ", true));
    }

    @Test
    void createWithExistingNameThrowsConflictWithoutSaving() {
        when(categoryRepository.existsByName("EPP")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.create(new CategoryRequest("EPP")))
                .isInstanceOf(ResourceConflictException.class);

        verify(categoryRepository, never()).saveAndFlush(any());
    }

    @Test
    void createTranslatesDatabaseUniqueViolationIntoConflict() {
        when(categoryRepository.existsByName("EPP")).thenReturn(false);
        when(categoryRepository.saveAndFlush(any(Category.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        assertThatThrownBy(() -> categoryService.create(new CategoryRequest("EPP")))
                .isInstanceOf(ResourceConflictException.class);
    }

    // read

    @Test
    void findAllReturnsActiveAndInactiveCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(
                category(1L, "EPP", true),
                category(2L, "Extintores", false)));

        assertThat(categoryService.findAll()).containsExactly(
                new CategoryResponse(1L, "EPP", true),
                new CategoryResponse(2L, "Extintores", false));
    }

    @Test
    void findByIdReturnsExistingCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category(1L, "EPP", true)));

        assertThat(categoryService.findById(1L)).isEqualTo(new CategoryResponse(1L, "EPP", true));
    }

    @Test
    void findByIdThrowsNotFoundWhenMissing() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // update

    @Test
    void updateChangesOnlyTheNameAndKeepsActiveFlag() {
        Category inactive = category(1L, "EPP", false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(inactive));
        when(categoryRepository.existsByNameAndIdNot("Gases", 1L)).thenReturn(false);
        when(categoryRepository.saveAndFlush(inactive)).thenReturn(inactive);

        CategoryResponse response = categoryService.update(1L, new CategoryRequest("Gases"));

        assertThat(response).isEqualTo(new CategoryResponse(1L, "Gases", false));
    }

    @Test
    void updateWithSameNameOfItselfIsAllowed() {
        Category existing = category(1L, "EPP", true);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsByNameAndIdNot("EPP", 1L)).thenReturn(false);
        when(categoryRepository.saveAndFlush(existing)).thenReturn(existing);

        assertThat(categoryService.update(1L, new CategoryRequest("EPP")).name()).isEqualTo("EPP");
    }

    @Test
    void updateThrowsNotFoundWhenMissing() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.update(99L, new CategoryRequest("EPP")))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(categoryRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateWithNameUsedByAnotherCategoryThrowsConflict() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category(1L, "EPP", true)));
        when(categoryRepository.existsByNameAndIdNot("Gases", 1L)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.update(1L, new CategoryRequest("Gases")))
                .isInstanceOf(ResourceConflictException.class);

        verify(categoryRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateTranslatesDatabaseUniqueViolationIntoConflict() {
        Category existing = category(1L, "EPP", true);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsByNameAndIdNot("Gases", 1L)).thenReturn(false);
        when(categoryRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        assertThatThrownBy(() -> categoryService.update(1L, new CategoryRequest("Gases")))
                .isInstanceOf(ResourceConflictException.class);
    }

    // activate / deactivate

    @Test
    void activateMarksInactiveCategoryAsActive() {
        Category inactive = category(1L, "EPP", false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(inactive));
        when(categoryRepository.save(inactive)).thenReturn(inactive);

        assertThat(categoryService.activate(1L)).isEqualTo(new CategoryResponse(1L, "EPP", true));
    }

    @Test
    void activateIsIdempotentWhenAlreadyActive() {
        Category active = category(1L, "EPP", true);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(active));
        when(categoryRepository.save(active)).thenReturn(active);

        assertThat(categoryService.activate(1L)).isEqualTo(new CategoryResponse(1L, "EPP", true));
        assertThat(categoryService.activate(1L)).isEqualTo(new CategoryResponse(1L, "EPP", true));
    }

    @Test
    void deactivateMarksActiveCategoryAsInactive() {
        Category active = category(1L, "EPP", true);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(active));
        when(categoryRepository.save(active)).thenReturn(active);

        assertThat(categoryService.deactivate(1L)).isEqualTo(new CategoryResponse(1L, "EPP", false));
    }

    @Test
    void deactivateIsIdempotentWhenAlreadyInactive() {
        Category inactive = category(1L, "EPP", false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(inactive));
        when(categoryRepository.save(inactive)).thenReturn(inactive);

        assertThat(categoryService.deactivate(1L)).isEqualTo(new CategoryResponse(1L, "EPP", false));
        assertThat(categoryService.deactivate(1L)).isEqualTo(new CategoryResponse(1L, "EPP", false));
    }

    @Test
    void activateAndDeactivateThrowNotFoundWhenMissing() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.activate(99L)).isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> categoryService.deactivate(99L)).isInstanceOf(ResourceNotFoundException.class);
    }
}
