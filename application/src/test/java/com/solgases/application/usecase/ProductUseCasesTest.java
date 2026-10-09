package com.solgases.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.solgases.application.exception.CategoryNotFoundException;
import com.solgases.domain.model.Category;
import com.solgases.application.exception.ConflictException;
import com.solgases.application.dto.ProductCommand;
import com.solgases.application.dto.ProductQuery;
import com.solgases.application.exception.DuplicateProductSkuException;
import com.solgases.application.exception.ProductNotFoundException;
import com.solgases.application.port.out.ProductPersistencePort;
import com.solgases.domain.model.Product;
import com.solgases.application.exception.UnitOfMeasureNotFoundException;
import com.solgases.domain.model.UnitOfMeasure;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductUseCasesTest {

    private static final Category CATEGORY = new Category(1L, "EPP", true);
    private static final UnitOfMeasure UNIT = new UnitOfMeasure(1L, "UN", "Unidad", true);
    private static final ProductCommand COMMAND = new ProductCommand("EPP-001", "Casco", "desc", "3M", "H-700",
            new BigDecimal("85000.00"), 1L, 1L);

    @Mock private ProductPersistencePort port;
    @InjectMocks private CreateProductService create;
    @InjectMocks private ListProductsService list;
    @InjectMocks private GetProductByIdService get;
    @InjectMocks private UpdateProductService update;
    @InjectMocks private ActivateProductService activate;
    @InjectMocks private DeactivateProductService deactivate;

    @Test
    void createSavesAnActiveProductWithResolvedReferences() {
        when(port.findCategoryById(1L)).thenReturn(Optional.of(CATEGORY));
        when(port.findUnitOfMeasureById(1L)).thenReturn(Optional.of(UNIT));
        Product saved = product(1L, true);
        when(port.saveNew(org.mockito.ArgumentMatchers.any(Product.class))).thenReturn(saved);
        assertThat(create.execute(COMMAND).sku()).isEqualTo("EPP-001");
    }

    @Test
    void createRejectsDuplicateSku() {
        when(port.existsBySku("EPP-001")).thenReturn(true);
        assertThatThrownBy(() -> create.execute(COMMAND)).isInstanceOf(DuplicateProductSkuException.class);
        verify(port, never()).saveNew(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void createRequiresExistingActiveReferences() {
        when(port.findCategoryById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> create.execute(COMMAND)).isInstanceOf(CategoryNotFoundException.class);

        when(port.findCategoryById(1L)).thenReturn(Optional.of(new Category(1L, "EPP", false)));
        assertThatThrownBy(() -> create.execute(COMMAND)).isInstanceOf(ConflictException.class);

        when(port.findCategoryById(1L)).thenReturn(Optional.of(CATEGORY));
        when(port.findUnitOfMeasureById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> create.execute(COMMAND)).isInstanceOf(UnitOfMeasureNotFoundException.class);
    }

    @Test
    void listPassesFiltersAndMapsProducts() {
        Product product = product(1L, true);
        when(port.findAll(new ProductQuery("casco", 1L, true))).thenReturn(List.of(product));
        assertThat(list.execute(new ProductQuery("casco", 1L, true))).hasSize(1);
    }

    @Test
    void getThrowsNotFoundWhenMissing() {
        when(port.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> get.execute(99L)).isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void updatePreservesActiveStateAndReplacesDetails() {
        Product existing = product(1L, false);
        Product updated = existing.withDetails(COMMAND.sku(), COMMAND.name(), COMMAND.description(), COMMAND.brand(),
                COMMAND.reference(), COMMAND.price(), CATEGORY, UNIT);
        when(port.findById(1L)).thenReturn(Optional.of(existing));
        when(port.findCategoryById(1L)).thenReturn(Optional.of(CATEGORY));
        when(port.findUnitOfMeasureById(1L)).thenReturn(Optional.of(UNIT));
        when(port.saveChanges(updated)).thenReturn(updated);
        assertThat(update.execute(1L, COMMAND).active()).isFalse();
    }

    @Test
    void updateDetectsDuplicateSkuAndMissingProduct() {
        when(port.findById(1L)).thenReturn(Optional.of(product(1L, true)));
        when(port.existsBySkuAndIdNot("EPP-001", 1L)).thenReturn(true);
        assertThatThrownBy(() -> update.execute(1L, COMMAND)).isInstanceOf(DuplicateProductSkuException.class);
        when(port.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> update.execute(99L, COMMAND)).isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void activateAndDeactivateUpdateProductState() {
        Product active = product(1L, true);
        Product inactive = active.deactivate();
        when(port.findById(1L)).thenReturn(Optional.of(active));
        when(port.saveChanges(inactive)).thenReturn(inactive);
        when(port.findById(2L)).thenReturn(Optional.of(product(2L, false)));
        when(port.saveChanges(product(2L, false).activate())).thenReturn(product(2L, true));
        assertThat(deactivate.execute(1L).active()).isFalse();
        assertThat(activate.execute(2L).active()).isTrue();
    }

    private static Product product(Long id, boolean active) {
        return new Product(id, "EPP-001", "Casco", "desc", "3M", "H-700", new BigDecimal("85000.00"), active,
                CATEGORY, UNIT);
    }
}
