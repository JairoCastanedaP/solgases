package com.solgases.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.solgases.category.entity.Category;
import com.solgases.exception.ResourceConflictException;
import com.solgases.exception.ResourceNotFoundException;
import com.solgases.inventory.dto.InventoryAdjustmentRequest;
import com.solgases.inventory.dto.InventoryMovementRequest;
import com.solgases.inventory.dto.InventoryMovementResponse;
import com.solgases.inventory.dto.InventoryResponse;
import com.solgases.inventory.entity.Inventory;
import com.solgases.inventory.entity.InventoryMovement;
import com.solgases.inventory.entity.MovementDirection;
import com.solgases.inventory.entity.MovementType;
import com.solgases.inventory.repository.InventoryMovementRepository;
import com.solgases.inventory.repository.InventoryRepository;
import com.solgases.product.entity.Product;
import com.solgases.product.repository.ProductRepository;
import com.solgases.unitofmeasure.entity.UnitOfMeasure;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryMovementRepository inventoryMovementRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private static Product product(Long id, boolean active) {
        Category category = new Category("EPP");
        ReflectionTestUtils.setField(category, "id", 1L);
        UnitOfMeasure unit = new UnitOfMeasure("UN", "Unidad");
        ReflectionTestUtils.setField(unit, "id", 1L);
        Product product = new Product("SKU-" + id, "Casco", null, null, null, category, unit, new BigDecimal("1"));
        ReflectionTestUtils.setField(product, "id", id);
        if (!active) {
            product.deactivate();
        }
        return product;
    }

    private static Inventory inventory(Product product, BigDecimal quantity) {
        Inventory inventory = new Inventory(product, quantity);
        ReflectionTestUtils.setField(inventory, "id", 1L);
        return inventory;
    }

    private static InventoryMovementRequest movementRequest(BigDecimal quantity) {
        return new InventoryMovementRequest(quantity, "Reason", "jcastaneda");
    }

    // registerEntry

    @Test
    void registerEntryCreatesInventoryOnFirstMovement() {
        Product product = product(1L, true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.empty());
        when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenAnswer(invocation -> {
            InventoryMovement movement = invocation.getArgument(0);
            ReflectionTestUtils.setField(movement, "id", 100L);
            return movement;
        });

        InventoryMovementResponse response = inventoryService.registerEntry(1L, movementRequest(new BigDecimal("10")));

        assertThat(response.type()).isEqualTo(MovementType.ENTRY);
        assertThat(response.direction()).isEqualTo(MovementDirection.INCREASE);
        assertThat(response.quantity()).isEqualByComparingTo("10");
        verify(inventoryRepository).saveAndFlush(argThatQuantity(new BigDecimal("10")));
    }

    @Test
    void registerEntryIncrementsExistingInventory() {
        Product product = product(1L, true);
        Inventory existing = inventory(product, new BigDecimal("5"));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(existing));
        when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        inventoryService.registerEntry(1L, movementRequest(new BigDecimal("3")));

        assertThat(existing.getCurrentQuantity()).isEqualByComparingTo("8");
    }

    @Test
    void registerEntryThrowsNotFoundWhenProductMissing() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryService.registerEntry(99L, movementRequest(BigDecimal.ONE)))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(inventoryRepository, never()).saveAndFlush(any());
    }

    @Test
    void registerEntryThrowsConflictWhenProductInactive() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product(1L, false)));

        assertThatThrownBy(() -> inventoryService.registerEntry(1L, movementRequest(BigDecimal.ONE)))
                .isInstanceOf(ResourceConflictException.class);
        verify(inventoryRepository, never()).saveAndFlush(any());
        verify(inventoryMovementRepository, never()).save(any());
    }

    @Test
    void registerEntryTranslatesUniqueViolationOnFirstInsertToConflict() {
        Product product = product(1L, true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.empty());
        when(inventoryRepository.saveAndFlush(any(Inventory.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        assertThatThrownBy(() -> inventoryService.registerEntry(1L, movementRequest(BigDecimal.ONE)))
                .isInstanceOf(ResourceConflictException.class);
        verify(inventoryMovementRepository, never()).save(any());
    }

    @Test
    void registerEntryTranslatesOptimisticLockFailureOnExistingInventoryToConflict() {
        Product product = product(1L, true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory(product, BigDecimal.TEN)));
        when(inventoryRepository.saveAndFlush(any(Inventory.class)))
                .thenThrow(new ObjectOptimisticLockingFailureException(Inventory.class, 1L));

        assertThatThrownBy(() -> inventoryService.registerEntry(1L, movementRequest(BigDecimal.ONE)))
                .isInstanceOf(ResourceConflictException.class);
        verify(inventoryMovementRepository, never()).save(any());
    }

    // registerExit

    @Test
    void registerExitDecrementsStock() {
        Product product = product(1L, true);
        Inventory existing = inventory(product, new BigDecimal("10"));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(existing));
        when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InventoryMovementResponse response = inventoryService.registerExit(1L, movementRequest(new BigDecimal("4")));

        assertThat(existing.getCurrentQuantity()).isEqualByComparingTo("6");
        assertThat(response.direction()).isEqualTo(MovementDirection.DECREASE);
    }

    @Test
    void registerExitLeavingExactlyZeroIsAllowed() {
        Product product = product(1L, true);
        Inventory existing = inventory(product, new BigDecimal("5"));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(existing));
        when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        inventoryService.registerExit(1L, movementRequest(new BigDecimal("5")));

        assertThat(existing.getCurrentQuantity()).isEqualByComparingTo("0");
    }

    @Test
    void registerExitWithInsufficientStockThrowsConflictWithoutPersisting() {
        Product product = product(1L, true);
        Inventory existing = inventory(product, new BigDecimal("5"));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> inventoryService.registerExit(1L, movementRequest(new BigDecimal("6"))))
                .isInstanceOf(ResourceConflictException.class);

        assertThat(existing.getCurrentQuantity()).isEqualByComparingTo("5");
        verify(inventoryRepository, never()).saveAndFlush(any());
        verify(inventoryMovementRepository, never()).save(any());
    }

    @Test
    void registerExitOnNewProductWithoutInventoryHasZeroAvailableAndIsRejected() {
        Product product = product(1L, true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryService.registerExit(1L, movementRequest(BigDecimal.ONE)))
                .isInstanceOf(ResourceConflictException.class);
        verify(inventoryRepository, never()).saveAndFlush(any());
    }

    // registerAdjustment

    @Test
    void registerAdjustmentIncreaseAddsToStock() {
        Product product = product(1L, true);
        Inventory existing = inventory(product, new BigDecimal("10"));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(existing));
        when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InventoryMovementResponse response = inventoryService.registerAdjustment(1L,
                new InventoryAdjustmentRequest(new BigDecimal("2"), MovementDirection.INCREASE, "Conteo", "user"));

        assertThat(existing.getCurrentQuantity()).isEqualByComparingTo("12");
        assertThat(response.type()).isEqualTo(MovementType.ADJUSTMENT);
        assertThat(response.direction()).isEqualTo(MovementDirection.INCREASE);
    }

    @Test
    void registerAdjustmentDecreaseSubtractsFromStock() {
        Product product = product(1L, true);
        Inventory existing = inventory(product, new BigDecimal("10"));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(existing));
        when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        inventoryService.registerAdjustment(1L,
                new InventoryAdjustmentRequest(new BigDecimal("4"), MovementDirection.DECREASE, "Conteo", "user"));

        assertThat(existing.getCurrentQuantity()).isEqualByComparingTo("6");
    }

    @Test
    void registerAdjustmentDecreaseLeavingExactlyZeroIsAllowed() {
        Product product = product(1L, true);
        Inventory existing = inventory(product, new BigDecimal("4"));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(existing));
        when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        inventoryService.registerAdjustment(1L,
                new InventoryAdjustmentRequest(new BigDecimal("4"), MovementDirection.DECREASE, "Conteo", "user"));

        assertThat(existing.getCurrentQuantity()).isEqualByComparingTo("0");
    }

    @Test
    void registerAdjustmentDecreaseWithInsufficientStockThrowsConflict() {
        Product product = product(1L, true);
        Inventory existing = inventory(product, new BigDecimal("2"));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> inventoryService.registerAdjustment(1L,
                new InventoryAdjustmentRequest(new BigDecimal("3"), MovementDirection.DECREASE, "Conteo", "user")))
                .isInstanceOf(ResourceConflictException.class);
        verify(inventoryRepository, never()).saveAndFlush(any());
    }

    @Test
    void registerAdjustmentThrowsConflictWhenProductInactive() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product(1L, false)));

        assertThatThrownBy(() -> inventoryService.registerAdjustment(1L,
                new InventoryAdjustmentRequest(BigDecimal.ONE, MovementDirection.INCREASE, "Conteo", "user")))
                .isInstanceOf(ResourceConflictException.class);
    }

    // getCurrentQuantity

    @Test
    void getCurrentQuantityReturnsZeroWithoutPersistingWhenNoInventoryYet() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product(1L, true)));
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.empty());

        InventoryResponse response = inventoryService.getCurrentQuantity(1L);

        assertThat(response).isEqualTo(new InventoryResponse(1L, BigDecimal.ZERO));
        verify(inventoryRepository, never()).save(any());
        verify(inventoryRepository, never()).saveAndFlush(any());
    }

    @Test
    void getCurrentQuantityReturnsActualValueWhenInventoryExists() {
        Product product = product(1L, true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory(product, new BigDecimal("42"))));

        assertThat(inventoryService.getCurrentQuantity(1L).currentQuantity()).isEqualByComparingTo("42");
    }

    @Test
    void getCurrentQuantityThrowsNotFoundWhenProductMissing() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryService.getCurrentQuantity(99L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getCurrentQuantityWorksEvenWhenProductIsInactive() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product(1L, false)));
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.empty());

        assertThat(inventoryService.getCurrentQuantity(1L).currentQuantity()).isEqualByComparingTo("0");
    }

    // getMovements / getMovement

    @Test
    void getMovementsReturnsListOrderedByRepository() {
        Product product = product(1L, true);
        InventoryMovement movement = InventoryMovement.entry(product, BigDecimal.ONE, "Reason", "user");
        ReflectionTestUtils.setField(movement, "id", 1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryMovementRepository.findAllByProductIdOrderByMovementDateDesc(1L)).thenReturn(List.of(movement));

        assertThat(inventoryService.getMovements(1L)).hasSize(1);
    }

    @Test
    void getMovementsThrowsNotFoundWhenProductMissing() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryService.getMovements(99L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getMovementReturnsExistingMovement() {
        Product product = product(1L, true);
        InventoryMovement movement = InventoryMovement.entry(product, BigDecimal.ONE, "Reason", "user");
        ReflectionTestUtils.setField(movement, "id", 5L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryMovementRepository.findByIdAndProductId(5L, 1L)).thenReturn(Optional.of(movement));

        assertThat(inventoryService.getMovement(1L, 5L).id()).isEqualTo(5L);
    }

    @Test
    void getMovementThrowsNotFoundWhenMovementDoesNotBelongToProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product(1L, true)));
        when(inventoryMovementRepository.findByIdAndProductId(5L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryService.getMovement(1L, 5L)).isInstanceOf(ResourceNotFoundException.class);
    }

    private static Inventory argThatQuantity(BigDecimal expected) {
        return org.mockito.ArgumentMatchers.argThat(inv -> inv.getCurrentQuantity().compareTo(expected) == 0);
    }
}
