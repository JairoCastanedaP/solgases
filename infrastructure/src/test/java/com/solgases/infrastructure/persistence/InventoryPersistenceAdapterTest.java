package com.solgases.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.solgases.application.exception.ConflictException;
import com.solgases.application.port.out.ProductPersistencePort;
import com.solgases.domain.model.Inventory;
import com.solgases.domain.model.InventoryMovement;
import com.solgases.domain.model.MovementDirection;
import com.solgases.domain.model.MovementType;
import com.solgases.infrastructure.persistence.entity.InventoryJpaEntity;
import com.solgases.infrastructure.persistence.entity.InventoryMovementJpaEntity;
import com.solgases.infrastructure.persistence.entity.ProductJpaEntity;
import com.solgases.infrastructure.persistence.repository.InventoryJpaRepository;
import com.solgases.infrastructure.persistence.repository.InventoryMovementJpaRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@ExtendWith(MockitoExtension.class)
class InventoryPersistenceAdapterTest {

    private static final long PRODUCT_ID = 7L;

    @Mock
    private InventoryJpaRepository inventoryRepository;
    @Mock
    private InventoryMovementJpaRepository movementRepository;
    @Mock
    private ProductPersistencePort productPersistencePort;
    @Mock
    private EntityManager entityManager;

    private InventoryPersistenceAdapter adapter;
    private ProductJpaEntity product;

    @BeforeEach
    void setUp() {
        adapter = new InventoryPersistenceAdapter(inventoryRepository, movementRepository, productPersistencePort,
                entityManager);
        product = mock(ProductJpaEntity.class);
        // Not every test maps the saved entity back to the domain
        lenient().when(product.getId()).thenReturn(PRODUCT_ID);
    }

    @Test
    void saveInventoryCreatesTheStockRowOnTheFirstMovement() {
        when(inventoryRepository.findByProductId(PRODUCT_ID)).thenReturn(Optional.empty());
        when(entityManager.getReference(ProductJpaEntity.class, PRODUCT_ID)).thenReturn(product);
        when(inventoryRepository.saveAndFlush(any(InventoryJpaEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Inventory saved = adapter.saveInventory(PRODUCT_ID, new BigDecimal("5.000"));

        assertThat(saved.productId()).isEqualTo(PRODUCT_ID);
        assertThat(saved.currentQuantity()).isEqualByComparingTo("5");
    }

    @Test
    void saveInventoryUpdatesTheExistingStockRow() {
        InventoryJpaEntity existing = new InventoryJpaEntity(product, new BigDecimal("2"));
        when(inventoryRepository.findByProductId(PRODUCT_ID)).thenReturn(Optional.of(existing));
        when(inventoryRepository.saveAndFlush(existing)).thenReturn(existing);

        assertThat(adapter.saveInventory(PRODUCT_ID, new BigDecimal("9")).currentQuantity())
                .isEqualByComparingTo("9");
        verify(entityManager, never()).getReference(any(), any());
    }

    @Test
    void concurrentStockUpdatesAreReportedAsConflicts() {
        InventoryJpaEntity existing = new InventoryJpaEntity(product, BigDecimal.ONE);
        when(inventoryRepository.findByProductId(PRODUCT_ID)).thenReturn(Optional.of(existing));
        when(inventoryRepository.saveAndFlush(existing))
                .thenThrow(new ObjectOptimisticLockingFailureException(InventoryJpaEntity.class, 1L))
                .thenThrow(new DataIntegrityViolationException("uk_inventory_product"));

        for (int attempt = 0; attempt < 2; attempt++) {
            assertThatThrownBy(() -> adapter.saveInventory(PRODUCT_ID, BigDecimal.TEN))
                    .isInstanceOf(ConflictException.class)
                    .hasMessageContaining("Concurrent inventory update detected for product 7");
        }
    }

    @Test
    void saveMovementBuildsEachTypeWithItsDirection() {
        when(entityManager.getReference(ProductJpaEntity.class, PRODUCT_ID)).thenReturn(product);
        when(movementRepository.save(any(InventoryMovementJpaEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        InventoryMovement entry = adapter.saveMovement(PRODUCT_ID, MovementType.ENTRY, null, BigDecimal.ONE, "r", "u");
        InventoryMovement exit = adapter.saveMovement(PRODUCT_ID, MovementType.EXIT, null, BigDecimal.ONE, "r", "u");
        InventoryMovement adjustment = adapter.saveMovement(PRODUCT_ID, MovementType.ADJUSTMENT,
                MovementDirection.DECREASE, BigDecimal.ONE, "count", "auditor");

        assertThat(entry.direction()).isEqualTo(MovementDirection.INCREASE);
        assertThat(exit.direction()).isEqualTo(MovementDirection.DECREASE);
        assertThat(adjustment).satisfies(movement -> {
            assertThat(movement.type()).isEqualTo(MovementType.ADJUSTMENT);
            assertThat(movement.direction()).isEqualTo(MovementDirection.DECREASE);
            assertThat(movement.productId()).isEqualTo(PRODUCT_ID);
            assertThat(movement.reason()).isEqualTo("count");
            assertThat(movement.responsibleUser()).isEqualTo("auditor");
            assertThat(movement.movementDate()).isNotNull();
        });
    }

    @Test
    void movementQueriesAreScopedToTheProduct() {
        when(entityManager.getReference(ProductJpaEntity.class, PRODUCT_ID)).thenReturn(product);
        when(movementRepository.save(any(InventoryMovementJpaEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        adapter.saveMovement(PRODUCT_ID, MovementType.ENTRY, null, BigDecimal.ONE, "r", "u");
        ArgumentCaptor<InventoryMovementJpaEntity> saved = ArgumentCaptor.forClass(InventoryMovementJpaEntity.class);
        verify(movementRepository).save(saved.capture());
        when(movementRepository.findAllByProductIdOrderByMovementDateDesc(PRODUCT_ID))
                .thenReturn(List.of(saved.getValue()));
        when(movementRepository.findByIdAndProductId(3L, PRODUCT_ID)).thenReturn(Optional.empty());

        assertThat(adapter.findMovementsByProductId(PRODUCT_ID)).singleElement()
                .extracting(InventoryMovement::type).isEqualTo(MovementType.ENTRY);
        assertThat(adapter.findMovementByIdAndProductId(3L, PRODUCT_ID)).isEmpty();
    }

    @Test
    void findByProductIdMapsTheStockRow() {
        when(inventoryRepository.findByProductId(PRODUCT_ID))
                .thenReturn(Optional.of(new InventoryJpaEntity(product, new BigDecimal("4"))));

        assertThat(adapter.findByProductId(PRODUCT_ID)).get()
                .extracting(Inventory::currentQuantity).isEqualTo(new BigDecimal("4"));
    }

    @Test
    void lockFailuresWhileSavingStockAreReportedAsConflicts() {
        InventoryJpaEntity existing = new InventoryJpaEntity(product, BigDecimal.ONE);
        when(inventoryRepository.findByProductId(PRODUCT_ID)).thenReturn(Optional.of(existing));
        when(inventoryRepository.saveAndFlush(existing))
                .thenThrow(new CannotAcquireLockException("Lock wait timeout exceeded"))
                .thenThrow(new PessimisticLockingFailureException("Deadlock found when trying to get lock"));

        for (int attempt = 0; attempt < 2; attempt++) {
            assertThatThrownBy(() -> adapter.saveInventory(PRODUCT_ID, BigDecimal.TEN))
                    .isExactlyInstanceOf(ConflictException.class)
                    .hasMessage("Concurrent inventory update detected for product 7; please retry");
        }
    }

    @Test
    void lockFailuresWhileSavingAMovementAreReportedAsConflicts() {
        when(entityManager.getReference(ProductJpaEntity.class, PRODUCT_ID)).thenReturn(product);
        when(movementRepository.save(any(InventoryMovementJpaEntity.class)))
                .thenThrow(new CannotAcquireLockException("Lock wait timeout exceeded"))
                .thenThrow(new PessimisticLockingFailureException("Deadlock found when trying to get lock"));

        for (int attempt = 0; attempt < 2; attempt++) {
            assertThatThrownBy(() -> adapter.saveMovement(PRODUCT_ID, MovementType.ENTRY, null, BigDecimal.ONE,
                    "r", "u"))
                    .isExactlyInstanceOf(ConflictException.class)
                    .hasMessage("Concurrent inventory update detected for product 7; please retry");
        }
    }

    @Test
    void databaseAvailabilityFailuresAreNotReportedAsConflicts() {
        DataAccessResourceFailureException connectionLost = new DataAccessResourceFailureException("Connection lost");
        InventoryJpaEntity existing = new InventoryJpaEntity(product, BigDecimal.ONE);
        when(inventoryRepository.findByProductId(PRODUCT_ID)).thenReturn(Optional.of(existing));
        when(inventoryRepository.saveAndFlush(existing)).thenThrow(connectionLost);
        when(entityManager.getReference(ProductJpaEntity.class, PRODUCT_ID)).thenReturn(product);
        when(movementRepository.save(any(InventoryMovementJpaEntity.class))).thenThrow(connectionLost);

        assertThatThrownBy(() -> adapter.saveInventory(PRODUCT_ID, BigDecimal.TEN)).isSameAs(connectionLost);
        assertThatThrownBy(() -> adapter.saveMovement(PRODUCT_ID, MovementType.ENTRY, null, BigDecimal.ONE, "r", "u"))
                .isSameAs(connectionLost);
    }
}
