package com.solgases.infrastructure.persistence;

import com.solgases.application.port.out.CategoryPersistencePort;
import com.solgases.domain.model.Category;
import com.solgases.application.dto.ProductQuery;
import com.solgases.application.exception.DuplicateProductSkuException;
import com.solgases.application.exception.ProductNotFoundException;
import com.solgases.application.port.out.ProductPersistencePort;
import com.solgases.domain.model.Product;
import com.solgases.infrastructure.persistence.repository.ProductJpaRepository;
import com.solgases.infrastructure.persistence.repository.ProductSpecifications;
import com.solgases.application.port.out.UnitOfMeasurePersistencePort;
import com.solgases.domain.model.UnitOfMeasure;
import com.solgases.infrastructure.persistence.mapper.ProductPersistenceMapper;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public class ProductPersistenceAdapter implements ProductPersistencePort {

    private final ProductJpaRepository productRepository;
    private final CategoryPersistencePort categoryPersistencePort;
    private final UnitOfMeasurePersistencePort unitOfMeasurePersistencePort;
    private final EntityManager entityManager;

    public ProductPersistenceAdapter(ProductJpaRepository productRepository,
            CategoryPersistencePort categoryPersistencePort,
            UnitOfMeasurePersistencePort unitOfMeasurePersistencePort,
            EntityManager entityManager) {
        this.productRepository = productRepository;
        this.categoryPersistencePort = categoryPersistencePort;
        this.unitOfMeasurePersistencePort = unitOfMeasurePersistencePort;
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id).map(ProductPersistenceMapper::toDomain);
    }

    @Override
    public List<Product> findAll(ProductQuery query) {
        Specification<com.solgases.infrastructure.persistence.entity.ProductJpaEntity> specification = Specification.allOf(
                ProductSpecifications.nameContains(query.name()),
                ProductSpecifications.hasCategoryId(query.categoryId()),
                ProductSpecifications.hasActive(query.active()),
                ProductSpecifications.fetchCategoryAndUnitOfMeasure());
        return productRepository.findAll(specification).stream().map(ProductPersistenceMapper::toDomain).toList();
    }

    @Override
    public Optional<Category> findCategoryById(Long id) {
        return categoryPersistencePort.findById(id);
    }

    @Override
    public Optional<UnitOfMeasure> findUnitOfMeasureById(Long id) {
        return unitOfMeasurePersistencePort.findById(id);
    }

    @Override
    public boolean existsBySku(String sku) { return productRepository.existsBySku(sku); }

    @Override
    public boolean existsBySkuAndIdNot(String sku, Long id) { return productRepository.existsBySkuAndIdNot(sku, id); }

    @Override
    public Product saveNew(Product product) {
        var entity = new com.solgases.infrastructure.persistence.entity.ProductJpaEntity(product.sku(), product.name(), product.description(),
                product.brand(), product.reference(),
                entityManager.getReference(com.solgases.infrastructure.persistence.entity.CategoryJpaEntity.class,
                        product.category().id()),
                entityManager.getReference(com.solgases.infrastructure.persistence.entity.UnitOfMeasureJpaEntity.class,
                        product.unitOfMeasure().id()), product.price());
        return saveOrConflict(entity);
    }

    @Override
    public Product saveChanges(Product product) {
        var entity = productRepository.findById(product.id())
                .orElseThrow(() -> new ProductNotFoundException(product.id()));
        entity.replaceDetails(product.sku(), product.name(), product.description(), product.brand(), product.reference(),
                entityManager.getReference(com.solgases.infrastructure.persistence.entity.CategoryJpaEntity.class,
                        product.category().id()),
                entityManager.getReference(com.solgases.infrastructure.persistence.entity.UnitOfMeasureJpaEntity.class,
                        product.unitOfMeasure().id()), product.price());
        if (product.active()) entity.activate(); else entity.deactivate();
        return saveOrConflict(entity);
    }

    private Product saveOrConflict(com.solgases.infrastructure.persistence.entity.ProductJpaEntity entity) {
        try {
            return ProductPersistenceMapper.toDomain(productRepository.saveAndFlush(entity));
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateProductSkuException(entity.getSku());
        }
    }
}
