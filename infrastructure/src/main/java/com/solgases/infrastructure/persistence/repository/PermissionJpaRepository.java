package com.solgases.infrastructure.persistence.repository;

import com.solgases.infrastructure.persistence.entity.PermissionJpaEntity;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionJpaRepository extends JpaRepository<PermissionJpaEntity, Long> {

    List<PermissionJpaEntity> findAllByOrderByIdAsc();

    List<PermissionJpaEntity> findAllByKeyIn(Collection<String> keys);

    boolean existsByKey(String key);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);
}
