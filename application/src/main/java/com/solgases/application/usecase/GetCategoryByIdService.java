package com.solgases.application.usecase;

import com.solgases.application.dto.CategoryResult;
import com.solgases.application.exception.CategoryNotFoundException;
import com.solgases.application.port.in.GetCategoryByIdUseCase;
import com.solgases.application.port.out.CategoryPersistencePort;
import com.solgases.domain.model.Category;
import org.springframework.transaction.annotation.Transactional;

public class GetCategoryByIdService implements GetCategoryByIdUseCase {

    private final CategoryPersistencePort categoryPersistencePort;

    public GetCategoryByIdService(CategoryPersistencePort categoryPersistencePort) {
        this.categoryPersistencePort = categoryPersistencePort;
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResult execute(Long id) {
        Category category = categoryPersistencePort.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        return CategoryResult.from(category);
    }
}
