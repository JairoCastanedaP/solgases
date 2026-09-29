package com.solgases.infrastructure.persistence.repository;

import com.solgases.infrastructure.persistence.entity.InventoryMovementJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryMovementJpaRepository extends JpaRepository<InventoryMovementJpaEntity, Long> {

    List<InventoryMovementJpaEntity> findAllByProductIdOrderByMovementDateDesc(Long productId);

    Optional<InventoryMovementJpaEntity> findByIdAndProductId(Long id, Long productId);
}
