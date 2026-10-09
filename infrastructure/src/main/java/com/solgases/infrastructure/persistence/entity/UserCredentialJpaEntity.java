package com.solgases.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Local password credential of a user, kept apart from {@link UserJpaEntity} and from the domain user.
 * Only an Argon2id hash is stored, never the password itself.
 */
@Entity
@Table(name = "tbl_user_credential")
public class UserCredentialJpaEntity {

    // Encoded Argon2 hashes are about 100 characters long; the column leaves room for stronger parameters
    public static final int PASSWORD_HASH_MAX_LENGTH = 255;

    @Id
    @Column(name = "user_id")
    private Long userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_credential_user"))
    private UserJpaEntity user;

    @Column(name = "password_hash", nullable = false, length = PASSWORD_HASH_MAX_LENGTH)
    private String passwordHash;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected UserCredentialJpaEntity() {
        // Required by JPA
    }

    public UserCredentialJpaEntity(UserJpaEntity user, String passwordHash) {
        this.user = user;
        this.passwordHash = passwordHash;
    }

    public Long getUserId() {
        return userId;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.MICROS);
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now().truncatedTo(ChronoUnit.MICROS);
    }

    @Override
    public String toString() {
        return "UserCredentialJpaEntity[userId=" + userId + ", passwordHash=****]";
    }
}
