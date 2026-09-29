package com.solgases.application.usecase;

import com.solgases.application.dto.CategoryResult;
import com.solgases.application.exception.CategoryNotFoundException;
import com.solgases.application.port.in.DeactivateCategoryUseCase;
import com.solgases.application.port.out.CategoryPersistencePort;
import com.solgases.domain.model.Category;
import org.springframework.transaction.annotation.Transactional;

public class DeactivateCategoryService implements DeactivateCategoryUseCase {

    private final CategoryPersistencePort categoryPersistencePort;

    public DeactivateCategoryService(CategoryPersistencePort categoryPersistencePort) {
        this.categoryPersistencePort = categoryPersistencePort;
    }

    @Override
    @Transactional
    public CategoryResult execute(Long id) {
        Category category = categoryPersistencePort.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        return CategoryResult.from(categoryPersistencePort.saveChanges(category.deactivate()));
    }
}
