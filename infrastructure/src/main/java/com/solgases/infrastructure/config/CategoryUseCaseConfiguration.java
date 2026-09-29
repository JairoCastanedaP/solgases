package com.solgases.infrastructure.config;

import com.solgases.application.port.out.CategoryPersistencePort;
import com.solgases.application.usecase.ActivateCategoryService;
import com.solgases.application.usecase.CreateCategoryService;
import com.solgases.application.usecase.DeactivateCategoryService;
import com.solgases.application.usecase.GetCategoryByIdService;
import com.solgases.application.usecase.ListCategoriesService;
import com.solgases.application.usecase.UpdateCategoryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CategoryUseCaseConfiguration {

    @Bean
    GetCategoryByIdService getCategoryByIdService(CategoryPersistencePort categoryPersistencePort) {
        return new GetCategoryByIdService(categoryPersistencePort);
    }

    @Bean
    CreateCategoryService createCategoryService(CategoryPersistencePort categoryPersistencePort) {
        return new CreateCategoryService(categoryPersistencePort);
    }

    @Bean
    ListCategoriesService listCategoriesService(CategoryPersistencePort categoryPersistencePort) {
        return new ListCategoriesService(categoryPersistencePort);
    }

    @Bean
    UpdateCategoryService updateCategoryService(CategoryPersistencePort categoryPersistencePort) {
        return new UpdateCategoryService(categoryPersistencePort);
    }

    @Bean
    ActivateCategoryService activateCategoryService(CategoryPersistencePort categoryPersistencePort) {
        return new ActivateCategoryService(categoryPersistencePort);
    }

    @Bean
    DeactivateCategoryService deactivateCategoryService(CategoryPersistencePort categoryPersistencePort) {
        return new DeactivateCategoryService(categoryPersistencePort);
    }
}
