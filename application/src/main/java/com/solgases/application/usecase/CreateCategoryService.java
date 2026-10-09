package com.solgases.application.usecase;

import com.solgases.application.dto.CategoryCommand;
import com.solgases.application.dto.CategoryResult;
import com.solgases.application.exception.DuplicateCategoryNameException;
import com.solgases.application.port.in.CreateCategoryUseCase;
import com.solgases.application.port.out.CategoryPersistencePort;
import org.springframework.transaction.annotation.Transactional;

public class CreateCategoryService implements CreateCategoryUseCase {

    private final CategoryPersistencePort categoryPersistencePort;

    public CreateCategoryService(CategoryPersistencePort categoryPersistencePort) {
        this.categoryPersistencePort = categoryPersistencePort;
    }

    @Override
    @Transactional
    public CategoryResult execute(CategoryCommand command) {
        if (categoryPersistencePort.existsByName(command.name())) {
            throw new DuplicateCategoryNameException(command.name());
        }
        return CategoryResult.from(categoryPersistencePort.saveNew(command.name()));
    }
}
