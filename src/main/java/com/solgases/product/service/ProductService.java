package com.solgases.product.service;

import com.solgases.category.entity.Category;
import com.solgases.category.repository.CategoryRepository;
import com.solgases.exception.ResourceConflictException;
import com.solgases.exception.ResourceNotFoundException;
import com.solgases.product.dto.ProductRequest;
import com.solgases.product.dto.ProductResponse;
import com.solgases.product.entity.Product;
import com.solgases.product.repository.ProductRepository;
import com.solgases.product.repository.ProductSpecifications;
import com.solgases.unitofmeasure.entity.UnitOfMeasure;
import com.solgases.unitofmeasure.repository.UnitOfMeasureRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository,
            UnitOfMeasureRepository unitOfMeasureRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsBySku(request.sku())) {
            throw duplicateSku(request.sku());
        }
        Category category = getActiveCategory(request.categoryId());
        UnitOfMeasure unitOfMeasure = getActiveUnitOfMeasure(request.unitOfMeasureId());

        Product product = new Product(request.sku(), request.name(), request.description(), request.brand(),
                request.reference(), category, unitOfMeasure, request.price());
        Product saved = saveOrConflict(product);
        log.info("Product created with id {}", saved.getId());
        return ProductResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll(String name, Long categoryId, Boolean active) {
        Specification<Product> specification = Specification.allOf(
                ProductSpecifications.nameContains(name),
                ProductSpecifications.hasCategoryId(categoryId),
                ProductSpecifications.hasActive(active),
                ProductSpecifications.fetchCategoryAndUnitOfMeasure());

        return productRepository.findAll(specification).stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return ProductResponse.from(getExisting(id));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getExisting(id);
        if (productRepository.existsBySkuAndIdNot(request.sku(), id)) {
            throw duplicateSku(request.sku());
        }
        Category category = getActiveCategory(request.categoryId());
        UnitOfMeasure unitOfMeasure = getActiveUnitOfMeasure(request.unitOfMeasureId());

        product.replaceDetails(request.sku(), request.name(), request.description(), request.brand(),
                request.reference(), category, unitOfMeasure, request.price());
        Product saved = saveOrConflict(product);
        log.info("Product {} updated", id);
        return ProductResponse.from(saved);
    }

    @Transactional
    public ProductResponse activate(Long id) {
        Product product = getExisting(id);
        product.activate();
        log.info("Product {} activated", id);
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public ProductResponse deactivate(Long id) {
        Product product = getExisting(id);
        product.deactivate();
        log.info("Product {} deactivated", id);
        return ProductResponse.from(productRepository.save(product));
    }

    private Product getExisting(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
    }

    private Category getActiveCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + categoryId));
        if (!category.isActive()) {
            throw new ResourceConflictException("Category with id " + categoryId + " is not active");
        }
        return category;
    }

    private UnitOfMeasure getActiveUnitOfMeasure(Long unitOfMeasureId) {
        UnitOfMeasure unitOfMeasure = unitOfMeasureRepository.findById(unitOfMeasureId)
                .orElseThrow(() -> new ResourceNotFoundException("Unit of measure not found with id " + unitOfMeasureId));
        if (!unitOfMeasure.isActive()) {
            throw new ResourceConflictException("Unit of measure with id " + unitOfMeasureId + " is not active");
        }
        return unitOfMeasure;
    }

    /**
     * Flushes immediately so that a violation of the database unique constraint on {@code sku}
     * surfaces here and can be reported as a conflict.
     */
    private Product saveOrConflict(Product product) {
        try {
            return productRepository.saveAndFlush(product);
        } catch (DataIntegrityViolationException ex) {
            throw duplicateSku(product.getSku());
        }
    }

    private ResourceConflictException duplicateSku(String sku) {
        return new ResourceConflictException("A product with the SKU '" + sku + "' already exists");
    }
}
