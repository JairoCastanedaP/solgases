package com.solgases.application.usecase;

import com.solgases.application.dto.CategoryResult;
import com.solgases.application.port.in.ListCategoriesUseCase;
import com.solgases.application.port.out.CategoryPersistencePort;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public class ListCategoriesService implements ListCategoriesUseCase {

    private final CategoryPersistencePort categoryPersistencePort;

    public ListCategoriesService(CategoryPersistencePort categoryPersistencePort) {
        this.categoryPersistencePort = categoryPersistencePort;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResult> execute() {
        return categoryPersistencePort.findAll().stream()
                .map(CategoryResult::from)
                .toList();
    }
}
