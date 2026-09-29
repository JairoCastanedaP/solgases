package com.solgases.infrastructure.persistence.mapper;

import com.solgases.domain.model.Inventory;
import com.solgases.domain.model.InventoryMovement;

public final class InventoryPersistenceMapper {
    private InventoryPersistenceMapper() {}

    public static Inventory toDomain(com.solgases.infrastructure.persistence.entity.InventoryJpaEntity entity) {
        return new Inventory(entity.getProduct().getId(), entity.getCurrentQuantity(), entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static InventoryMovement toDomain(
            com.solgases.infrastructure.persistence.entity.InventoryMovementJpaEntity entity) {
        return new InventoryMovement(entity.getId(), entity.getProduct().getId(), entity.getType(),
                entity.getDirection(), entity.getQuantity(), entity.getReason(), entity.getResponsibleUser(),
                entity.getMovementDate());
    }
}
