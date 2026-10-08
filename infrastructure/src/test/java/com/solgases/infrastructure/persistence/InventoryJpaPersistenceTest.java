package com.solgases.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.solgases.domain.model.Inventory;
import com.solgases.domain.model.InventoryMovement;
import com.solgases.domain.model.MovementDirection;
import com.solgases.domain.model.MovementType;
import com.solgases.domain.model.Product;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Comparator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

/** Persistence of stock and inventory movements against a real JPA provider and an in-memory H2 database. */
@DataJpaTest(showSql = false)
@Import({InventoryPersistenceAdapter.class, ProductPersistenceAdapter.class, CategoryPersistenceAdapter.class,
        UnitOfMeasurePersistenceAdapter.class})
class InventoryJpaPersistenceTest {

    @Autowired
    private InventoryPersistenceAdapter adapter;
    @Autowired
    private ProductPersistenceAdapter productAdapter;
    @Autowired
    private CategoryPersistenceAdapter categoryAdapter;
    @Autowired
    private UnitOfMeasurePersistenceAdapter unitAdapter;
    @Autowired
    private EntityManager entityManager;

    private Long productId;
    private Long otherProductId;

    @BeforeEach
    void setUp() {
        var category = categoryAdapter.saveNew("Gases");
        var unit = unitAdapter.saveNew("UN", "Unit");
        productId = productAdapter.saveNew(new Product(null, "SKU-1", "Oxygen", null, null, null, BigDecimal.TEN,
                true, category, unit)).id();
        otherProductId = productAdapter.saveNew(new Product(null, "SKU-2", "Argon", null, null, null, BigDecimal.TEN,
                true, category, unit)).id();
    }

    private long stockRows() {
        return entityManager.createQuery("select count(i) from InventoryJpaEntity i", Long.class).getSingleResult();
    }

    @Test
    void productWithoutMovementsHasNoStockRow() {
        assertThat(adapter.findByProductId(productId)).isEmpty();
        assertThat(adapter.findProductById(productId)).isPresent();
    }

    @Test
    void stockRowIsCreatedOnceAndThenUpdatedWithItsScale() {
        adapter.saveInventory(productId, new BigDecimal("5"));
        adapter.saveInventory(productId, new BigDecimal("2.125"));
        entityManager.clear();

        Inventory inventory = adapter.findByProductId(productId).orElseThrow();
        assertThat(inventory.currentQuantity()).isEqualTo(new BigDecimal("2.125"));
        assertThat(inventory.createdAt()).isNotNull();
        assertThat(inventory.updatedAt()).isNotNull();
        assertThat(stockRows()).isEqualTo(1);
    }

    @Test
    void maximumSupportedStockFitsTheQuantityColumn() {
        adapter.saveInventory(productId, Inventory.MAX_QUANTITY);
        entityManager.clear();

        assertThat(adapter.findByProductId(productId)).get()
                .extracting(Inventory::currentQuantity).isEqualTo(Inventory.MAX_QUANTITY);
    }

    @Test
    void movementsArePersistedAndListedPerProductMostRecentFirst() {
        InventoryMovement entry = adapter.saveMovement(productId, MovementType.ENTRY, MovementDirection.INCREASE,
                new BigDecimal("10"), "Purchase", "warehouse");
        InventoryMovement adjustment = adapter.saveMovement(productId, MovementType.ADJUSTMENT,
                MovementDirection.DECREASE, new BigDecimal("1.5"), "Count", "auditor");
        adapter.saveMovement(otherProductId, MovementType.ENTRY, MovementDirection.INCREASE, BigDecimal.ONE,
                "Purchase", "warehouse");
        entityManager.flush();
        entityManager.clear();

        // Two movements may share the same timestamp, so the order is checked on the dates themselves
        assertThat(adapter.findMovementsByProductId(productId))
                .extracting(InventoryMovement::id).containsExactlyInAnyOrder(adjustment.id(), entry.id());
        assertThat(adapter.findMovementsByProductId(productId))
                .extracting(InventoryMovement::movementDate)
                .isSortedAccordingTo(Comparator.reverseOrder());
        assertThat(adapter.findMovementByIdAndProductId(adjustment.id(), productId)).get()
                .satisfies(movement -> {
                    assertThat(movement.type()).isEqualTo(MovementType.ADJUSTMENT);
                    assertThat(movement.direction()).isEqualTo(MovementDirection.DECREASE);
                    assertThat(movement.quantity()).isEqualByComparingTo("1.5");
                    assertThat(movement.reason()).isEqualTo("Count");
                    assertThat(movement.responsibleUser()).isEqualTo("auditor");
                });
        // A movement is only found through the product it belongs to
        assertThat(adapter.findMovementByIdAndProductId(entry.id(), otherProductId)).isEmpty();
    }

    @Test
    void movementForAMissingProductIsRejectedByTheForeignKey() {
        assertThatThrownBy(() -> {
            adapter.saveMovement(999L, MovementType.ENTRY, MovementDirection.INCREASE, BigDecimal.ONE, "r", "u");
            entityManager.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }
}
