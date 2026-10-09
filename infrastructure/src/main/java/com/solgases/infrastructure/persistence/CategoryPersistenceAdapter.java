package com.solgases.infrastructure.persistence;

import com.solgases.application.exception.CategoryNotFoundException;
import com.solgases.application.exception.DuplicateCategoryNameException;
import com.solgases.application.port.out.CategoryPersistencePort;
import com.solgases.domain.model.Category;
import com.solgases.infrastructure.persistence.repository.CategoryJpaRepository;
import com.solgases.infrastructure.persistence.mapper.CategoryPersistenceMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryPersistenceAdapter implements CategoryPersistencePort {

    private final CategoryJpaRepository categoryRepository;

    public CategoryPersistenceAdapter(CategoryJpaRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id).map(CategoryPersistenceMapper::toDomain);
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll().stream().map(CategoryPersistenceMapper::toDomain).toList();
    }

    @Override
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    @Override
    public boolean existsByNameAndIdNot(String name, Long id) {
        return categoryRepository.existsByNameAndIdNot(name, id);
    }

    @Override
    public Category saveNew(String name) {
        try {
            return CategoryPersistenceMapper.toDomain(categoryRepository.saveAndFlush(
                    CategoryPersistenceMapper.toNewEntity(name)));
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateCategoryNameException(name);
        }
    }

    @Override
    public Category saveChanges(Category category) {
        com.solgases.infrastructure.persistence.entity.CategoryJpaEntity entity = categoryRepository.findById(category.id())
                .orElseThrow(() -> new CategoryNotFoundException(category.id()));
        entity.rename(category.name());
        if (category.active()) {
            entity.activate();
        } else {
            entity.deactivate();
        }
        try {
            return CategoryPersistenceMapper.toDomain(categoryRepository.saveAndFlush(entity));
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateCategoryNameException(category.name());
        }
    }
}
