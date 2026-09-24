package com.solgases.inventory.entity;

import com.solgases.product.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * The current stock of a product. Created lazily, on the first {@link InventoryMovement}
 * registered for its product; a product without any movement yet has no row here.
 */
@Entity
@Table(
        name = "tbl_inventory",
        uniqueConstraints = @UniqueConstraint(name = "uk_inventory_product", columnNames = "product_id"))
public class Inventory {

    public static final int QUANTITY_INTEGER_DIGITS = 12;
    public static final int QUANTITY_SCALE = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(name = "current_quantity", nullable = false, precision = 15, scale = QUANTITY_SCALE)
    private BigDecimal currentQuantity;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Inventory() {
        // Required by JPA
    }

    public Inventory(Product product, BigDecimal currentQuantity) {
        this.product = product;
        this.currentQuantity = currentQuantity;
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public BigDecimal getCurrentQuantity() {
        return currentQuantity;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void applyQuantity(BigDecimal newQuantity) {
        this.currentQuantity = newQuantity;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
