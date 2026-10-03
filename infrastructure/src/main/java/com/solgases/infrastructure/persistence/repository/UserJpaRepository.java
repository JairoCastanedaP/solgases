package com.solgases.infrastructure.persistence.repository;

import com.solgases.infrastructure.persistence.entity.UserJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    @Query("select u from UserJpaEntity u left join fetch u.roles where u.id = :id")
    Optional<UserJpaEntity> findWithRolesById(@Param("id") Long id);

    @Query("select distinct u from UserJpaEntity u left join fetch u.roles order by u.id")
    List<UserJpaEntity> findAllWithRoles();

    boolean existsByUsername(String username);

    boolean existsByUsernameAndIdNot(String username, Long id);
}
