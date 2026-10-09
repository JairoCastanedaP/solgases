package com.solgases.infrastructure.persistence.repository;

import com.solgases.infrastructure.persistence.entity.InventoryJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryJpaRepository extends JpaRepository<InventoryJpaEntity, Long> {

    Optional<InventoryJpaEntity> findByProductId(Long productId);
}
