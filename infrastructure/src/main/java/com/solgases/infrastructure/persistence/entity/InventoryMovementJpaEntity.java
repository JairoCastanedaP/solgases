package com.solgases.infrastructure.persistence.entity;

import com.solgases.infrastructure.persistence.entity.ProductJpaEntity;
import com.solgases.domain.model.MovementDirection;
import com.solgases.domain.model.MovementType;
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
 * {@code product_id}, independent of whether an {@link InventoryJpaEntity} row exists yet.
 *
 * <p>Instances are built exclusively through {@link #entry}, {@link #exit} and {@link #adjustment}
 * so that only the {@link MovementType}/{@link MovementDirection} combinations documented as valid
 * (ENTRY-INCREASE, EXIT-DECREASE, ADJUSTMENT-INCREASE, ADJUSTMENT-DECREASE) can ever be constructed.
 */
@Entity
@Table(
        name = "tbl_inventory_movement",
        indexes = @Index(name = "idx_inventory_movement_product", columnList = "product_id"))
public class InventoryMovementJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductJpaEntity product;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20, columnDefinition = "VARCHAR(20)")
    private MovementType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false, length = 20, columnDefinition = "VARCHAR(20)")
    private MovementDirection direction;

    @Column(name = "quantity", nullable = false, precision = 15, scale = InventoryJpaEntity.QUANTITY_SCALE)
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

    protected InventoryMovementJpaEntity() {
        // Required by JPA
    }

    private InventoryMovementJpaEntity(ProductJpaEntity product, MovementType type, MovementDirection direction,
            BigDecimal quantity, String reason, String responsibleUser) {
        this.product = product;
        this.type = type;
        this.direction = direction;
        this.quantity = quantity;
        this.reason = reason;
        this.responsibleUser = responsibleUser;
        this.movementDate = Instant.now();
    }

    public static InventoryMovementJpaEntity entry(ProductJpaEntity product, BigDecimal quantity, String reason, String responsibleUser) {
        return new InventoryMovementJpaEntity(product, MovementType.ENTRY, MovementDirection.INCREASE, quantity, reason, responsibleUser);
    }

    public static InventoryMovementJpaEntity exit(ProductJpaEntity product, BigDecimal quantity, String reason, String responsibleUser) {
        return new InventoryMovementJpaEntity(product, MovementType.EXIT, MovementDirection.DECREASE, quantity, reason, responsibleUser);
    }

    public static InventoryMovementJpaEntity adjustment(ProductJpaEntity product, MovementDirection direction, BigDecimal quantity,
            String reason, String responsibleUser) {
        return new InventoryMovementJpaEntity(product, MovementType.ADJUSTMENT, direction, quantity, reason, responsibleUser);
    }

    public Long getId() {
        return id;
    }

    public ProductJpaEntity getProduct() {
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
