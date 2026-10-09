package com.solgases.infrastructure.persistence.repository;

import com.solgases.infrastructure.persistence.entity.UserCredentialJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserCredentialJpaRepository extends JpaRepository<UserCredentialJpaEntity, Long> {

    @Query("select c from UserCredentialJpaEntity c where c.user.username = :username")
    Optional<UserCredentialJpaEntity> findByUsername(@Param("username") String username);
}
