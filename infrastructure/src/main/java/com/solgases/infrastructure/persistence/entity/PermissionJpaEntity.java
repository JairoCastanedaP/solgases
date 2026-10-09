package com.solgases.infrastructure.persistence.entity;

import com.solgases.domain.model.Permission;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity
@Table(
        name = "tbl_permission",
        uniqueConstraints = {
                @UniqueConstraint(name = PermissionJpaEntity.UK_KEY, columnNames = "permission_key"),
                @UniqueConstraint(name = PermissionJpaEntity.UK_CODE, columnNames = "code")
        })
public class PermissionJpaEntity {

    public static final String UK_KEY = "uk_permission_key";
    public static final String UK_CODE = "uk_permission_code";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // "key" is a reserved word in MySQL, hence the column name
    @Column(name = "permission_key", nullable = false, updatable = false, length = Permission.KEY_MAX_LENGTH)
    private String key;

    @Column(name = "code", nullable = false, length = Permission.CODE_MAX_LENGTH)
    private String code;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected PermissionJpaEntity() {
        // Required by JPA
    }

    public PermissionJpaEntity(String key, String code) {
        this.key = key;
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getCode() {
        return code;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void changeCode(String code) {
        this.code = code;
    }

    // Truncated to the DATETIME(6) precision of the column, so responses match what is read back later
    private static Instant now() {
        return Instant.now().truncatedTo(ChronoUnit.MICROS);
    }

    @PrePersist
    void onCreate() {
        Instant now = now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = now();
    }
}
