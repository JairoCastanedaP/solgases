package com.solgases.inventory.repository;

import com.solgases.inventory.entity.InventoryMovement;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {

    List<InventoryMovement> findAllByProductIdOrderByMovementDateDesc(Long productId);

    Optional<InventoryMovement> findByIdAndProductId(Long id, Long productId);
}
