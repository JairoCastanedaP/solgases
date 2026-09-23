package com.solgases.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.solgases.category.entity.Category;
import com.solgases.category.repository.CategoryRepository;
import com.solgases.exception.ResourceConflictException;
import com.solgases.exception.ResourceNotFoundException;
import com.solgases.product.dto.ProductRequest;
import com.solgases.product.dto.ProductResponse;
import com.solgases.product.entity.Product;
import com.solgases.product.repository.ProductRepository;
import com.solgases.unitofmeasure.entity.UnitOfMeasure;
import com.solgases.unitofmeasure.repository.UnitOfMeasureRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UnitOfMeasureRepository unitOfMeasureRepository;

    @InjectMocks
    private ProductService productService;

    private static Category category(Long id, boolean active) {
        Category category = new Category("EPP");
        ReflectionTestUtils.setField(category, "id", id);
        if (!active) {
            category.deactivate();
        }
        return category;
    }

    private static UnitOfMeasure unit(Long id, boolean active) {
        UnitOfMeasure unit = new UnitOfMeasure("UN", "Unidad");
        ReflectionTestUtils.setField(unit, "id", id);
        if (!active) {
            unit.deactivate();
        }
        return unit;
    }

    private static Product product(Long id, String sku, Category category, UnitOfMeasure unit, boolean active) {
        Product product = new Product(sku, "Casco", "desc", "3M", "H-700", category, unit, new BigDecimal("85000.00"));
        ReflectionTestUtils.setField(product, "id", id);
        if (!active) {
            product.deactivate();
        }
        return product;
    }

    private static ProductRequest request(String sku, Long categoryId, Long unitId) {
        return new ProductRequest(sku, "Casco", "desc", "3M", "H-700", new BigDecimal("85000.00"), categoryId, unitId);
    }

    // create

    @Test
    void createSavesActiveProductWhenCategoryAndUnitAreActive() {
        Category cat = category(1L, true);
        UnitOfMeasure unit = unit(1L, true);
        when(productRepository.existsBySku("EPP-001")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));
        when(unitOfMeasureRepository.findById(1L)).thenReturn(Optional.of(unit));
        when(productRepository.saveAndFlush(any(Product.class))).thenAnswer(invocation -> {
            Product toSave = invocation.getArgument(0);
            ReflectionTestUtils.setField(toSave, "id", 10L);
            return toSave;
        });

        ProductResponse response = productService.create(request("EPP-001", 1L, 1L));

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.active()).isTrue();
        assertThat(response.category().id()).isEqualTo(1L);
        assertThat(response.unitOfMeasure().id()).isEqualTo(1L);
    }

    @Test
    void createWithDuplicateSkuThrowsConflictWithoutTouchingCategoryOrUnit() {
        when(productRepository.existsBySku("EPP-001")).thenReturn(true);

        assertThatThrownBy(() -> productService.create(request("EPP-001", 1L, 1L)))
                .isInstanceOf(ResourceConflictException.class);

        verify(categoryRepository, never()).findById(any());
        verify(productRepository, never()).saveAndFlush(any());
    }

    @Test
    void createTranslatesDatabaseUniqueViolationIntoConflict() {
        when(productRepository.existsBySku("EPP-001")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category(1L, true)));
        when(unitOfMeasureRepository.findById(1L)).thenReturn(Optional.of(unit(1L, true)));
        when(productRepository.saveAndFlush(any(Product.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        assertThatThrownBy(() -> productService.create(request("EPP-001", 1L, 1L)))
                .isInstanceOf(ResourceConflictException.class);
    }

    @Test
    void createWithNonExistingCategoryThrowsNotFound() {
        when(productRepository.existsBySku("EPP-001")).thenReturn(false);
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.create(request("EPP-001", 99L, 1L)))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(productRepository, never()).saveAndFlush(any());
    }

    @Test
    void createWithInactiveCategoryThrowsConflict() {
        when(productRepository.existsBySku("EPP-001")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category(1L, false)));

        assertThatThrownBy(() -> productService.create(request("EPP-001", 1L, 1L)))
                .isInstanceOf(ResourceConflictException.class);

        verify(productRepository, never()).saveAndFlush(any());
    }

    @Test
    void createWithNonExistingUnitOfMeasureThrowsNotFound() {
        when(productRepository.existsBySku("EPP-001")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category(1L, true)));
        when(unitOfMeasureRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.create(request("EPP-001", 1L, 99L)))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(productRepository, never()).saveAndFlush(any());
    }

    @Test
    void createWithInactiveUnitOfMeasureThrowsConflict() {
        when(productRepository.existsBySku("EPP-001")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category(1L, true)));
        when(unitOfMeasureRepository.findById(1L)).thenReturn(Optional.of(unit(1L, false)));

        assertThatThrownBy(() -> productService.create(request("EPP-001", 1L, 1L)))
                .isInstanceOf(ResourceConflictException.class);

        verify(productRepository, never()).saveAndFlush(any());
    }

    // read

    @Test
    void findAllDelegatesToRepositoryWithASpecification() {
        when(productRepository.findAll(org.mockito.ArgumentMatchers.<Specification<Product>>any()))
                .thenReturn(List.of(product(1L, "EPP-001", category(1L, true), unit(1L, true), true)));

        List<ProductResponse> result = productService.findAll("casco", 1L, true);

        assertThat(result).hasSize(1);
        verify(productRepository).findAll(org.mockito.ArgumentMatchers.<Specification<Product>>any());
    }

    @Test
    void findByIdReturnsExistingProduct() {
        Product existing = product(1L, "EPP-001", category(1L, true), unit(1L, true), true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThat(productService.findById(1L).sku()).isEqualTo("EPP-001");
    }

    @Test
    void findByIdThrowsNotFoundWhenMissing() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(99L)).isInstanceOf(ResourceNotFoundException.class);
    }

    // update

    @Test
    void updateChangesFieldsAndKeepsActiveFlag() {
        Category cat = category(1L, true);
        UnitOfMeasure unit = unit(1L, true);
        Product existing = product(1L, "EPP-001", cat, unit, false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.existsBySkuAndIdNot("EPP-002", 1L)).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));
        when(unitOfMeasureRepository.findById(1L)).thenReturn(Optional.of(unit));
        when(productRepository.saveAndFlush(existing)).thenReturn(existing);

        ProductResponse response = productService.update(1L, request("EPP-002", 1L, 1L));

        assertThat(response.sku()).isEqualTo("EPP-002");
        assertThat(response.active()).isFalse();
    }

    @Test
    void updateCanChangeCategoryAndUnitOfMeasure() {
        Category originalCategory = category(1L, true);
        UnitOfMeasure originalUnit = unit(1L, true);
        Category newCategory = category(2L, true);
        UnitOfMeasure newUnit = unit(2L, true);
        Product existing = product(1L, "EPP-001", originalCategory, originalUnit, true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.existsBySkuAndIdNot("EPP-001", 1L)).thenReturn(false);
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(newCategory));
        when(unitOfMeasureRepository.findById(2L)).thenReturn(Optional.of(newUnit));
        when(productRepository.saveAndFlush(existing)).thenReturn(existing);

        ProductResponse response = productService.update(1L, request("EPP-001", 2L, 2L));

        assertThat(response.category().id()).isEqualTo(2L);
        assertThat(response.unitOfMeasure().id()).isEqualTo(2L);
    }

    @Test
    void updateWithOwnSkuIsAllowed() {
        Category cat = category(1L, true);
        UnitOfMeasure unit = unit(1L, true);
        Product existing = product(1L, "EPP-001", cat, unit, true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.existsBySkuAndIdNot("EPP-001", 1L)).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));
        when(unitOfMeasureRepository.findById(1L)).thenReturn(Optional.of(unit));
        when(productRepository.saveAndFlush(existing)).thenReturn(existing);

        assertThat(productService.update(1L, request("EPP-001", 1L, 1L)).sku()).isEqualTo("EPP-001");
    }

    @Test
    void updateThrowsNotFoundWhenProductMissing() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(99L, request("EPP-001", 1L, 1L)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateWithSkuUsedByAnotherProductThrowsConflict() {
        Product existing = product(1L, "EPP-001", category(1L, true), unit(1L, true), true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.existsBySkuAndIdNot("EPP-002", 1L)).thenReturn(true);

        assertThatThrownBy(() -> productService.update(1L, request("EPP-002", 1L, 1L)))
                .isInstanceOf(ResourceConflictException.class);

        verify(productRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateWithInactiveCategoryThrowsConflict() {
        Product existing = product(1L, "EPP-001", category(1L, true), unit(1L, true), true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.existsBySkuAndIdNot("EPP-001", 1L)).thenReturn(false);
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category(2L, false)));

        assertThatThrownBy(() -> productService.update(1L, request("EPP-001", 2L, 1L)))
                .isInstanceOf(ResourceConflictException.class);
    }

    @Test
    void updateWithInactiveUnitOfMeasureThrowsConflict() {
        Product existing = product(1L, "EPP-001", category(1L, true), unit(1L, true), true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.existsBySkuAndIdNot("EPP-001", 1L)).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category(1L, true)));
        when(unitOfMeasureRepository.findById(2L)).thenReturn(Optional.of(unit(2L, false)));

        assertThatThrownBy(() -> productService.update(1L, request("EPP-001", 1L, 2L)))
                .isInstanceOf(ResourceConflictException.class);
    }

    // activate / deactivate

    @Test
    void activateIsIdempotentWhenAlreadyActive() {
        Product active = product(1L, "EPP-001", category(1L, true), unit(1L, true), true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(active));
        when(productRepository.save(active)).thenReturn(active);

        assertThat(productService.activate(1L).active()).isTrue();
        assertThat(productService.activate(1L).active()).isTrue();
    }

    @Test
    void deactivateIsIdempotentWhenAlreadyInactive() {
        Product inactive = product(1L, "EPP-001", category(1L, true), unit(1L, true), false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(inactive));
        when(productRepository.save(inactive)).thenReturn(inactive);

        assertThat(productService.deactivate(1L).active()).isFalse();
        assertThat(productService.deactivate(1L).active()).isFalse();
    }

    @Test
    void activateAndDeactivateThrowNotFoundWhenMissing() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.activate(99L)).isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> productService.deactivate(99L)).isInstanceOf(ResourceNotFoundException.class);
    }
}
