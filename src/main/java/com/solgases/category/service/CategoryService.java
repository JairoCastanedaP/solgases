package com.solgases.category.service;

import com.solgases.category.dto.CategoryRequest;
import com.solgases.category.dto.CategoryResponse;
import com.solgases.category.entity.Category;
import com.solgases.category.repository.CategoryRepository;
import com.solgases.exception.ResourceConflictException;
import com.solgases.exception.ResourceNotFoundException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw duplicateName(request.name());
        }
        Category category = new Category(request.name());
        Category saved = saveOrConflict(category);
        log.info("Category created with id {}", saved.getId());
        return CategoryResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse findById(Long id) {
        return CategoryResponse.from(getExisting(id));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = getExisting(id);
        if (categoryRepository.existsByNameAndIdNot(request.name(), id)) {
            throw duplicateName(request.name());
        }
        category.rename(request.name());
        Category saved = saveOrConflict(category);
        log.info("Category {} updated", id);
        return CategoryResponse.from(saved);
    }

    @Transactional
    public CategoryResponse activate(Long id) {
        Category category = getExisting(id);
        category.activate();
        log.info("Category {} activated", id);
        return CategoryResponse.from(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse deactivate(Long id) {
        Category category = getExisting(id);
        category.deactivate();
        log.info("Category {} deactivated", id);
        return CategoryResponse.from(categoryRepository.save(category));
    }

    private Category getExisting(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + id));
    }

    /**
     * Flushes immediately so that a violation of the database unique constraint (for example,
     * two concurrent requests with the same name) surfaces here and can be reported as a conflict.
     */
    private Category saveOrConflict(Category category) {
        try {
            return categoryRepository.saveAndFlush(category);
        } catch (DataIntegrityViolationException ex) {
            throw duplicateName(category.getName());
        }
    }

    private ResourceConflictException duplicateName(String name) {
        return new ResourceConflictException("A category with the name '" + name + "' already exists");
    }
}
