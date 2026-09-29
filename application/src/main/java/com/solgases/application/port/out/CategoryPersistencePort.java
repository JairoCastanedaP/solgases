package com.solgases.application.port.out;

import com.solgases.domain.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryPersistencePort {

    Optional<Category> findById(Long id);

    List<Category> findAll();

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    Category saveNew(String name);

    Category saveChanges(Category category);
}
