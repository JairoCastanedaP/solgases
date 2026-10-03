package com.solgases.infrastructure.persistence.repository;

import com.solgases.infrastructure.persistence.entity.RoleJpaEntity;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleJpaRepository extends JpaRepository<RoleJpaEntity, Long> {

    @Query("select r from RoleJpaEntity r left join fetch r.permissions where r.id = :id")
    Optional<RoleJpaEntity> findWithPermissionsById(@Param("id") Long id);

    @Query("select distinct r from RoleJpaEntity r left join fetch r.permissions order by r.id")
    List<RoleJpaEntity> findAllWithPermissions();

    @Query("select distinct r from RoleJpaEntity r left join fetch r.permissions where r.id in :ids")
    List<RoleJpaEntity> findAllWithPermissionsByIdIn(@Param("ids") Collection<Long> ids);

    boolean existsByKey(String key);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
