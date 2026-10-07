package com.solgases.infrastructure.persistence.entity;

import com.solgases.domain.model.User;
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

/** An internal user. It stores no password or any other credential. */
@Entity
@Table(
        name = "tbl_user",
        uniqueConstraints = @UniqueConstraint(name = UserJpaEntity.UK_USERNAME, columnNames = "username"))
public class UserJpaEntity {

    public static final String UK_USERNAME = "uk_user_username";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, length = User.USERNAME_MAX_LENGTH)
    private String username;

    @Column(name = "display_name", nullable = false, length = User.DISPLAY_NAME_MAX_LENGTH)
    private String displayName;

    @Column(name = "active", nullable = false)
    private boolean active;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tbl_user_role",
            joinColumns = @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_role_user")),
            inverseJoinColumns = @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "fk_user_role_role")))
    private Set<RoleJpaEntity> roles = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected UserJpaEntity() {
        // Required by JPA
    }

    public UserJpaEntity(String username, String displayName) {
        this.username = username;
        this.displayName = displayName;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isActive() {
        return active;
    }

    public Set<RoleJpaEntity> getRoles() {
        return roles;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void replaceDetails(String username, String displayName) {
        this.username = username;
        this.displayName = displayName;
    }

    /**
     * Replaces the roles only when the set of role ids actually changes. A change limited to the join table
     * does not trigger {@link PreUpdate} on this row, so updatedAt is set explicitly here.
     */
    public void replaceRoles(Collection<RoleJpaEntity> newRoles) {
        if (ids(roles).equals(ids(newRoles))) {
            return;
        }
        roles.clear();
        roles.addAll(newRoles);
        this.updatedAt = now();
    }

    private static Set<Long> ids(Collection<RoleJpaEntity> roles) {
        return roles.stream().map(RoleJpaEntity::getId).collect(Collectors.toSet());
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
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
