package com.solgases.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.solgases.application.exception.ConflictException;
import com.solgases.application.dto.InventoryAdjustmentCommand;
import com.solgases.application.dto.InventoryMovementCommand;
import com.solgases.application.port.out.InventoryPersistencePort;
import com.solgases.application.usecase.GetInventoryService;
import com.solgases.application.usecase.GetInventoryMovementService;
import com.solgases.application.usecase.ListInventoryMovementsService;
import com.solgases.application.usecase.RegisterInventoryAdjustmentService;
import com.solgases.application.usecase.RegisterInventoryEntryService;
import com.solgases.application.usecase.RegisterInventoryExitService;
import com.solgases.domain.model.Inventory;
import com.solgases.domain.model.InventoryMovement;
import com.solgases.domain.model.MovementDirection;
import com.solgases.domain.model.MovementType;
import com.solgases.application.exception.ProductNotFoundException;
import com.solgases.domain.model.Product;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InventoryMovementUseCaseTest {
    @Mock InventoryPersistencePort persistence;

    private Product product(boolean active) {
        return new Product(1L, "SKU", "Product", null, null, null, BigDecimal.ONE, active, null, null);
    }

    @Test void firstEntryStartsAtZeroAndPersistsInventoryBeforeMovement() {
        when(persistence.findProductById(1L)).thenReturn(Optional.of(product(true)));
        when(persistence.findByProductId(1L)).thenReturn(Optional.empty());
        when(persistence.saveMovement(1L, MovementType.ENTRY, MovementDirection.INCREASE,
                new BigDecimal("5.000"), "Purchase", "user"))
                .thenReturn(new InventoryMovement(7L, 1L, MovementType.ENTRY, MovementDirection.INCREASE,
                        new BigDecimal("5.000"), "Purchase", "user", Instant.EPOCH));

        var result = new RegisterInventoryEntryService(persistence).execute(1L,
                new InventoryMovementCommand(new BigDecimal("5.000"), "Purchase", "user"));

        assertThat(result.id()).isEqualTo(7L);
        var order = inOrder(persistence);
        order.verify(persistence).saveInventory(1L, new BigDecimal("5.000"));
        order.verify(persistence).saveMovement(1L, MovementType.ENTRY, MovementDirection.INCREASE,
                new BigDecimal("5.000"), "Purchase", "user");
    }

    @Test void missingInventoryIsReportedAsZeroAndMissingProductIs404() {
        when(persistence.findProductById(1L)).thenReturn(Optional.of(product(true)));
        when(persistence.findByProductId(1L)).thenReturn(Optional.empty());
        assertThat(new GetInventoryService(persistence).execute(1L).currentQuantity()).isEqualByComparingTo("0");
        when(persistence.findProductById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> new GetInventoryService(persistence).execute(2L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test void exitRejectsNegativeStockWithoutWritingAnything() {
        when(persistence.findProductById(1L)).thenReturn(Optional.of(product(true)));
        when(persistence.findByProductId(1L)).thenReturn(Optional.of(Inventory.empty(1L).withQuantity(new BigDecimal("2"))));
        assertThatThrownBy(() -> new RegisterInventoryExitService(persistence).execute(1L,
                new InventoryMovementCommand(new BigDecimal("3"), "Sale", "user")))
                .isInstanceOf(ConflictException.class).hasMessageContaining("Insufficient stock");
        verify(persistence, never()).saveInventory(anyLong(), any());
        verify(persistence, never()).saveMovement(anyLong(), any(), any(), any(), any(), any());
    }

    @Test void exitDecreasesStockAndStoresExitMovement() {
        when(persistence.findProductById(1L)).thenReturn(Optional.of(product(true)));
        when(persistence.findByProductId(1L)).thenReturn(Optional.of(Inventory.empty(1L).withQuantity(new BigDecimal("8"))));
        when(persistence.saveMovement(1L, MovementType.EXIT, MovementDirection.DECREASE,
                new BigDecimal("3"), "Sale", "user"))
                .thenReturn(new InventoryMovement(8L, 1L, MovementType.EXIT, MovementDirection.DECREASE,
                        new BigDecimal("3"), "Sale", "user", Instant.EPOCH));
        var result = new RegisterInventoryExitService(persistence).execute(1L,
                new InventoryMovementCommand(new BigDecimal("3"), "Sale", "user"));
        assertThat(result.type()).isEqualTo(MovementType.EXIT);
        verify(persistence).saveInventory(1L, new BigDecimal("5"));
    }

    @Test void adjustmentsApplyEitherDirection() {
        when(persistence.findProductById(1L)).thenReturn(Optional.of(product(true)));
        when(persistence.findByProductId(1L)).thenReturn(Optional.of(Inventory.empty(1L).withQuantity(new BigDecimal("5"))));
        when(persistence.saveMovement(eq(1L), eq(MovementType.ADJUSTMENT), any(), eq(new BigDecimal("2")), any(), any()))
                .thenAnswer(call -> new InventoryMovement(9L, 1L, MovementType.ADJUSTMENT,
                        call.getArgument(2), call.getArgument(3), call.getArgument(4), call.getArgument(5), Instant.EPOCH));
        var service = new RegisterInventoryAdjustmentService(persistence);
        service.execute(1L, new InventoryAdjustmentCommand(new BigDecimal("2"), MovementDirection.INCREASE, "Count", "user"));
        service.execute(1L, new InventoryAdjustmentCommand(new BigDecimal("2"), MovementDirection.DECREASE, "Count", "user"));
        verify(persistence).saveInventory(1L, new BigDecimal("7"));
        verify(persistence).saveInventory(1L, new BigDecimal("3"));
    }

    @Test void decreaseAdjustmentCannotProduceNegativeStock() {
        when(persistence.findProductById(1L)).thenReturn(Optional.of(product(true)));
        when(persistence.findByProductId(1L)).thenReturn(Optional.of(Inventory.empty(1L).withQuantity(BigDecimal.ONE)));
        assertThatThrownBy(() -> new RegisterInventoryAdjustmentService(persistence).execute(1L,
                new InventoryAdjustmentCommand(new BigDecimal("2"), MovementDirection.DECREASE, "Count", "user")))
                .isInstanceOf(ConflictException.class).hasMessageContaining("Insufficient stock");
        verify(persistence, never()).saveMovement(anyLong(), any(), any(), any(), any(), any());
    }

    @Test void listingAndGettingMovementsUseProductScopedPersistenceCalls() {
        when(persistence.findProductById(1L)).thenReturn(Optional.of(product(true)));
        var movement = new InventoryMovement(11L, 1L, MovementType.ENTRY, MovementDirection.INCREASE,
                BigDecimal.ONE, "Purchase", "user", Instant.EPOCH);
        when(persistence.findMovementsByProductId(1L)).thenReturn(java.util.List.of(movement));
        when(persistence.findMovementByIdAndProductId(11L, 1L)).thenReturn(Optional.of(movement));
        assertThat(new ListInventoryMovementsService(persistence).execute(1L)).containsExactly(
                com.solgases.application.dto.InventoryMovementResult.from(movement));
        assertThat(new GetInventoryMovementService(persistence).execute(1L, 11L).id()).isEqualTo(11L);
        verify(persistence).findMovementByIdAndProductId(11L, 1L);
    }

    @Test void inactiveProductRejectsMovement() {
        when(persistence.findProductById(1L)).thenReturn(Optional.of(product(false)));
        assertThatThrownBy(() -> new RegisterInventoryAdjustmentService(persistence).execute(1L,
                new InventoryAdjustmentCommand(BigDecimal.ONE, MovementDirection.INCREASE, "Count", "user")))
                .isInstanceOf(ConflictException.class).hasMessage("Product with id 1 is not active");
    }

    @Test void entryMayReachExactlyTheMaximumSupportedStock() {
        when(persistence.findProductById(1L)).thenReturn(Optional.of(product(true)));
        when(persistence.findByProductId(1L))
                .thenReturn(Optional.of(Inventory.empty(1L).withQuantity(new BigDecimal("999999999998.999"))));
        when(persistence.saveMovement(1L, MovementType.ENTRY, MovementDirection.INCREASE, BigDecimal.ONE,
                "Purchase", "user"))
                .thenReturn(new InventoryMovement(12L, 1L, MovementType.ENTRY, MovementDirection.INCREASE,
                        BigDecimal.ONE, "Purchase", "user", Instant.EPOCH));

        new RegisterInventoryEntryService(persistence).execute(1L,
                new InventoryMovementCommand(BigDecimal.ONE, "Purchase", "user"));

        verify(persistence).saveInventory(1L, Inventory.MAX_QUANTITY);
    }

    @Test void entryExceedingTheMaximumSupportedStockIsRejectedWithoutWritingAnything() {
        when(persistence.findProductById(1L)).thenReturn(Optional.of(product(true)));
        when(persistence.findByProductId(1L))
                .thenReturn(Optional.of(Inventory.empty(1L).withQuantity(Inventory.MAX_QUANTITY)));

        assertThatThrownBy(() -> new RegisterInventoryEntryService(persistence).execute(1L,
                new InventoryMovementCommand(new BigDecimal("0.001"), "Purchase", "user")))
                .isExactlyInstanceOf(ConflictException.class)
                .hasMessage("Stock for product 1 would exceed the maximum supported quantity 999999999999.999: "
                        + "available 999999999999.999, requested 0.001")
                .hasMessageNotContaining("retry");
        verify(persistence, never()).saveInventory(anyLong(), any());
        verify(persistence, never()).saveMovement(anyLong(), any(), any(), any(), any(), any());
    }

    @Test void increaseAdjustmentExceedingTheMaximumSupportedStockIsRejected() {
        when(persistence.findProductById(1L)).thenReturn(Optional.of(product(true)));
        when(persistence.findByProductId(1L))
                .thenReturn(Optional.of(Inventory.empty(1L).withQuantity(new BigDecimal("999999999999"))));

        assertThatThrownBy(() -> new RegisterInventoryAdjustmentService(persistence).execute(1L,
                new InventoryAdjustmentCommand(BigDecimal.ONE, MovementDirection.INCREASE, "Count", "user")))
                .isExactlyInstanceOf(ConflictException.class)
                .hasMessageStartingWith("Stock for product 1 would exceed the maximum supported quantity");
        verify(persistence, never()).saveInventory(anyLong(), any());
        verify(persistence, never()).saveMovement(anyLong(), any(), any(), any(), any(), any());
    }
}
