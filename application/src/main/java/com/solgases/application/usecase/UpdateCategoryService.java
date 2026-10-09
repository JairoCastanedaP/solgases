package com.solgases.application.usecase;

import com.solgases.application.dto.CategoryCommand;
import com.solgases.application.dto.CategoryResult;
import com.solgases.application.exception.CategoryNotFoundException;
import com.solgases.application.exception.DuplicateCategoryNameException;
import com.solgases.application.port.in.UpdateCategoryUseCase;
import com.solgases.application.port.out.CategoryPersistencePort;
import com.solgases.domain.model.Category;
import org.springframework.transaction.annotation.Transactional;

public class UpdateCategoryService implements UpdateCategoryUseCase {

    private final CategoryPersistencePort categoryPersistencePort;

    public UpdateCategoryService(CategoryPersistencePort categoryPersistencePort) {
        this.categoryPersistencePort = categoryPersistencePort;
    }

    @Override
    @Transactional
    public CategoryResult execute(Long id, CategoryCommand command) {
        Category category = categoryPersistencePort.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        if (categoryPersistencePort.existsByNameAndIdNot(command.name(), id)) {
            throw new DuplicateCategoryNameException(command.name());
        }
        return CategoryResult.from(categoryPersistencePort.saveChanges(category.withName(command.name())));
    }
}
