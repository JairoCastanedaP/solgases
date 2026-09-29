package com.solgases.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.solgases.application.port.out.CategoryPersistencePort;
import com.solgases.application.exception.DuplicateProductSkuException;
import com.solgases.domain.model.Product;
import com.solgases.infrastructure.persistence.repository.ProductJpaRepository;
import com.solgases.application.port.out.UnitOfMeasurePersistencePort;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class ProductPersistenceAdapterTest {

    @Mock private ProductJpaRepository productRepository;
    @Mock private CategoryPersistencePort categoryPersistencePort;
    @Mock private UnitOfMeasurePersistencePort unitOfMeasurePersistencePort;
    @Mock private EntityManager entityManager;
    @InjectMocks private ProductPersistenceAdapter adapter;

    @Test
    void saveNewTranslatesDatabaseUniqueViolationToDuplicateSku() {
        when(entityManager.getReference(com.solgases.infrastructure.persistence.entity.CategoryJpaEntity.class, 1L))
                .thenReturn(new com.solgases.infrastructure.persistence.entity.CategoryJpaEntity("EPP"));
        when(entityManager.getReference(com.solgases.infrastructure.persistence.entity.UnitOfMeasureJpaEntity.class, 1L))
                .thenReturn(new com.solgases.infrastructure.persistence.entity.UnitOfMeasureJpaEntity("UN", "Unidad"));
        when(productRepository.saveAndFlush(any(com.solgases.infrastructure.persistence.entity.ProductJpaEntity.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate entry"));
        Product product = new Product(null, "EPP-001", "Casco", null, null, null, new BigDecimal("1.00"), true,
                new com.solgases.domain.model.Category(1L, "EPP", true),
                new com.solgases.domain.model.UnitOfMeasure(1L, "UN", "Unidad", true));

        assertThatThrownBy(() -> adapter.saveNew(product))
                .isInstanceOf(DuplicateProductSkuException.class)
                .hasMessage("A product with the SKU 'EPP-001' already exists");
    }
}
