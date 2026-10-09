package com.solgases.infrastructure.persistence.repository;

import com.solgases.infrastructure.persistence.entity.UnitOfMeasureJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnitOfMeasureJpaRepository extends JpaRepository<UnitOfMeasureJpaEntity, Long> {

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);
}
