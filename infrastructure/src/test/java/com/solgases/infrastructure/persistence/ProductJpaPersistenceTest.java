package com.solgases.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.solgases.application.dto.ProductQuery;
import com.solgases.application.exception.DuplicateProductSkuException;
import com.solgases.domain.model.Category;
import com.solgases.domain.model.Product;
import com.solgases.domain.model.UnitOfMeasure;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

/** Persistence of products, their relations and search filters against a real JPA provider and H2. */
@DataJpaTest(showSql = false)
@Import({ProductPersistenceAdapter.class, CategoryPersistenceAdapter.class, UnitOfMeasurePersistenceAdapter.class})
class ProductJpaPersistenceTest {

    @Autowired
    private ProductPersistenceAdapter adapter;
    @Autowired
    private CategoryPersistenceAdapter categoryAdapter;
    @Autowired
    private UnitOfMeasurePersistenceAdapter unitAdapter;
    @Autowired
    private EntityManager entityManager;

    private Category gases;
    private Category tools;
    private UnitOfMeasure unit;

    @BeforeEach
    void setUp() {
        gases = categoryAdapter.saveNew("Gases");
        tools = categoryAdapter.saveNew("Tools");
        unit = unitAdapter.saveNew("UN", "Unit");
    }

    private Product newProduct(String sku, String name, Category category) {
        return new Product(null, sku, name, "Description", "Brand", "Ref", new BigDecimal("10.50"), true,
                category, unit);
    }

    @Test
    void newProductIsPersistedWithItsRelations() {
        Product saved = adapter.saveNew(newProduct("SKU-1", "Oxygen cylinder", gases));
        entityManager.clear();

        Product found = adapter.findById(saved.id()).orElseThrow();

        assertThat(found.sku()).isEqualTo("SKU-1");
        assertThat(found.active()).isTrue();
        assertThat(found.price()).isEqualByComparingTo("10.50");
        assertThat(found.category()).isEqualTo(gases);
        assertThat(found.unitOfMeasure()).isEqualTo(unit);
    }

    @Test
    void changesReplaceDetailsRelationsAndActiveFlag() {
        Product saved = adapter.saveNew(newProduct("SKU-1", "Oxygen cylinder", gases));

        adapter.saveChanges(new Product(saved.id(), "SKU-2", "Toolbox", null, null, null, new BigDecimal("99.99"),
                false, tools, unit));
        entityManager.clear();

        Product found = adapter.findById(saved.id()).orElseThrow();
        assertThat(found.sku()).isEqualTo("SKU-2");
        assertThat(found.description()).isNull();
        assertThat(found.active()).isFalse();
        assertThat(found.category()).isEqualTo(tools);
    }

    @Test
    void filtersCanBeCombinedAndNameMatchIsCaseInsensitive() {
        Product oxygen = adapter.saveNew(newProduct("SKU-1", "Oxygen cylinder", gases));
        Product argon = adapter.saveNew(newProduct("SKU-2", "Argon cylinder", gases));
        Product wrench = adapter.saveNew(newProduct("SKU-3", "Wrench", tools));
        adapter.saveChanges(new Product(argon.id(), argon.sku(), argon.name(), argon.description(), argon.brand(),
                argon.reference(), argon.price(), false, gases, unit));
        entityManager.clear();

        assertThat(adapter.findAll(new ProductQuery(null, null, null))).hasSize(3);
        assertThat(adapter.findAll(new ProductQuery("CYLINDER", null, null)))
                .extracting(Product::id).containsExactlyInAnyOrder(oxygen.id(), argon.id());
        assertThat(adapter.findAll(new ProductQuery(null, tools.id(), null)))
                .extracting(Product::id).containsExactly(wrench.id());
        assertThat(adapter.findAll(new ProductQuery("cylinder", gases.id(), true)))
                .extracting(Product::id).containsExactly(oxygen.id());
    }

    @Test
    void wildcardCharactersInTheNameFilterAreMatchedLiterally() {
        adapter.saveNew(newProduct("SKU-1", "Gas 100% pure", gases));
        adapter.saveNew(newProduct("SKU-2", "Gas 1000 pure", gases));

        assertThat(adapter.findAll(new ProductQuery("100%", null, null)))
                .extracting(Product::sku).containsExactly("SKU-1");
    }

    @Test
    void databaseRejectsDuplicateSkus() {
        adapter.saveNew(newProduct("SKU-1", "Oxygen cylinder", gases));

        assertThatThrownBy(() -> adapter.saveNew(newProduct("SKU-1", "Other", tools)))
                .isInstanceOf(DuplicateProductSkuException.class);
    }

    @Test
    void skuExistenceChecksExcludeTheProductItself() {
        Product saved = adapter.saveNew(newProduct("SKU-1", "Oxygen cylinder", gases));

        assertThat(adapter.existsBySku("SKU-1")).isTrue();
        assertThat(adapter.existsBySkuAndIdNot("SKU-1", saved.id())).isFalse();
    }
}
