package com.solgases.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class CatalogInventoryModelTest {

    private final Category category = new Category(1L, "Gases", true);
    private final UnitOfMeasure unit = new UnitOfMeasure(2L, "KG", "Kilogram", true);

    @Test
    void categoryRenameKeepsIdAndActiveFlag() {
        Category inactive = category.deactivate();

        assertThat(inactive.withName("Industrial gases")).isEqualTo(new Category(1L, "Industrial gases", false));
        assertThat(inactive.activate()).isEqualTo(category);
    }

    @Test
    void unitOfMeasureUpdateKeepsIdAndActiveFlag() {
        UnitOfMeasure inactive = unit.deactivate();

        assertThat(inactive.update("LB", "Pound")).isEqualTo(new UnitOfMeasure(2L, "LB", "Pound", false));
        assertThat(inactive.activate()).isEqualTo(unit);
    }

    @Test
    void productWithDetailsKeepsIdAndActiveFlag() {
        Product product = new Product(3L, "SKU-1", "Oxygen", null, null, null, BigDecimal.TEN, false, category, unit);
        Category otherCategory = new Category(4L, "Welding", true);

        Product changed = product.withDetails("SKU-2", "Argon", "Cylinder", "Brand", "Ref", BigDecimal.ONE,
                otherCategory, unit);

        assertThat(changed).isEqualTo(new Product(3L, "SKU-2", "Argon", "Cylinder", "Brand", "Ref", BigDecimal.ONE,
                false, otherCategory, unit));
        assertThat(changed.activate().active()).isTrue();
        assertThat(changed.activate().deactivate()).isEqualTo(changed);
    }

    @Test
    void emptyInventoryStartsAtZeroAndQuantityChangesKeepTimestamps() {
        Instant created = Instant.parse("2026-01-01T00:00:00Z");
        Instant updated = Instant.parse("2026-01-02T00:00:00Z");

        assertThat(Inventory.empty(3L)).isEqualTo(new Inventory(3L, BigDecimal.ZERO, null, null));
        assertThat(new Inventory(3L, BigDecimal.ONE, created, updated).withQuantity(BigDecimal.TEN))
                .isEqualTo(new Inventory(3L, BigDecimal.TEN, created, updated));
    }
}
