package com.solgases.inventory.entity;

import com.solgases.product.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * A single, immutable entry in the history of stock changes of a product. Referenced directly by
 * {@code product_id}, independent of whether an {@link Inventory} row exists yet.
 *
 * <p>Instances are built exclusively through {@link #entry}, {@link #exit} and {@link #adjustment}
 * so that only the {@link MovementType}/{@link MovementDirection} combinations documented as valid
 * (ENTRY-INCREASE, EXIT-DECREASE, ADJUSTMENT-INCREASE, ADJUSTMENT-DECREASE) can ever be constructed.
 */
@Entity
@Table(
        name = "tbl_inventory_movement",
        indexes = @Index(name = "idx_inventory_movement_product", columnList = "product_id"))
public class InventoryMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20, columnDefinition = "VARCHAR(20)")
    private MovementType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false, length = 20, columnDefinition = "VARCHAR(20)")
    private MovementDirection direction;

    @Column(name = "quantity", nullable = false, precision = 15, scale = Inventory.QUANTITY_SCALE)
    private BigDecimal quantity;

    @Column(name = "reason", nullable = false, length = 255)
    private String reason;

    @Column(name = "responsible_user", nullable = false, length = 255)
    private String responsibleUser;

    @Column(name = "movement_date", nullable = false)
    private Instant movementDate;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected InventoryMovement() {
        // Required by JPA
    }

    private InventoryMovement(Product product, MovementType type, MovementDirection direction,
            BigDecimal quantity, String reason, String responsibleUser) {
        this.product = product;
        this.type = type;
        this.direction = direction;
        this.quantity = quantity;
        this.reason = reason;
        this.responsibleUser = responsibleUser;
        this.movementDate = Instant.now();
    }

    public static InventoryMovement entry(Product product, BigDecimal quantity, String reason, String responsibleUser) {
        return new InventoryMovement(product, MovementType.ENTRY, MovementDirection.INCREASE, quantity, reason, responsibleUser);
    }

    public static InventoryMovement exit(Product product, BigDecimal quantity, String reason, String responsibleUser) {
        return new InventoryMovement(product, MovementType.EXIT, MovementDirection.DECREASE, quantity, reason, responsibleUser);
    }

    public static InventoryMovement adjustment(Product product, MovementDirection direction, BigDecimal quantity,
            String reason, String responsibleUser) {
        return new InventoryMovement(product, MovementType.ADJUSTMENT, direction, quantity, reason, responsibleUser);
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public MovementType getType() {
        return type;
    }

    public MovementDirection getDirection() {
        return direction;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public String getReason() {
        return reason;
    }

    public String getResponsibleUser() {
        return responsibleUser;
    }

    public Instant getMovementDate() {
        return movementDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
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
