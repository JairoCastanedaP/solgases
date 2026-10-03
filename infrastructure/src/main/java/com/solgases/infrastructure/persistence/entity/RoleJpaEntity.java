package com.solgases.infrastructure.persistence.entity;

import com.solgases.domain.model.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(
        name = "tbl_role",
        uniqueConstraints = {
                @UniqueConstraint(name = RoleJpaEntity.UK_KEY, columnNames = "role_key"),
                @UniqueConstraint(name = RoleJpaEntity.UK_NAME, columnNames = "name")
        })
public class RoleJpaEntity {

    public static final String UK_KEY = "uk_role_key";
    public static final String UK_NAME = "uk_role_name";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // "key" is a reserved word in MySQL, hence the column name
    @Column(name = "role_key", nullable = false, updatable = false, length = Role.KEY_MAX_LENGTH)
    private String key;

    @Column(name = "name", nullable = false, length = Role.NAME_MAX_LENGTH)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tbl_role_permission",
            joinColumns = @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "fk_role_permission_role")),
            inverseJoinColumns = @JoinColumn(name = "permission_id",
                    foreignKey = @ForeignKey(name = "fk_role_permission_permission")))
    private Set<PermissionJpaEntity> permissions = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected RoleJpaEntity() {
        // Required by JPA
    }

    public RoleJpaEntity(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public Set<PermissionJpaEntity> getPermissions() {
        return permissions;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void rename(String name) {
        this.name = name;
    }

    /**
     * Replaces the permissions only when the set of permission ids actually changes. A change limited to the
     * join table does not trigger {@link PreUpdate} on this row, so updatedAt is set explicitly here.
     */
    public void replacePermissions(Collection<PermissionJpaEntity> newPermissions) {
        if (ids(permissions).equals(ids(newPermissions))) {
            return;
        }
        permissions.clear();
        permissions.addAll(newPermissions);
        this.updatedAt = now();
    }

    private static Set<Long> ids(Collection<PermissionJpaEntity> permissions) {
        return permissions.stream().map(PermissionJpaEntity::getId).collect(Collectors.toSet());
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
