package com.solgases.infrastructure.persistence;

import com.solgases.application.exception.ConflictException;
import com.solgases.application.port.out.InventoryPersistencePort;
import com.solgases.domain.model.Inventory;
import com.solgases.domain.model.MovementDirection;
import com.solgases.domain.model.MovementType;
import com.solgases.infrastructure.persistence.entity.InventoryMovementJpaEntity;
import com.solgases.infrastructure.persistence.repository.InventoryMovementJpaRepository;
import com.solgases.infrastructure.persistence.repository.InventoryJpaRepository;
import com.solgases.infrastructure.persistence.mapper.InventoryPersistenceMapper;
import com.solgases.application.port.out.ProductPersistencePort;
import com.solgases.domain.model.Product;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Repository;

@Repository
public class InventoryPersistenceAdapter implements InventoryPersistencePort {

    private final InventoryJpaRepository inventoryRepository;
    private final InventoryMovementJpaRepository movementRepository;
    private final ProductPersistencePort productPersistencePort;
    private final EntityManager entityManager;

    public InventoryPersistenceAdapter(InventoryJpaRepository inventoryRepository,
            InventoryMovementJpaRepository movementRepository, ProductPersistencePort productPersistencePort,
            EntityManager entityManager) {
        this.inventoryRepository = inventoryRepository;
        this.movementRepository = movementRepository;
        this.productPersistencePort = productPersistencePort;
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Product> findProductById(Long id) { return productPersistencePort.findById(id); }

    @Override
    public Optional<Inventory> findByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId).map(InventoryPersistenceMapper::toDomain);
    }

    @Override
    public List<com.solgases.domain.model.InventoryMovement> findMovementsByProductId(Long productId) {
        return movementRepository.findAllByProductIdOrderByMovementDateDesc(productId).stream()
                .map(InventoryPersistenceMapper::toDomain).toList();
    }

    @Override
    public Optional<com.solgases.domain.model.InventoryMovement> findMovementByIdAndProductId(
            Long movementId, Long productId) {
        return movementRepository.findByIdAndProductId(movementId, productId).map(InventoryPersistenceMapper::toDomain);
    }

    @Override
    public Inventory saveInventory(Long productId, BigDecimal quantity) {
        var inventory = inventoryRepository.findByProductId(productId)
                .orElseGet(() -> new com.solgases.infrastructure.persistence.entity.InventoryJpaEntity(
                        entityManager.getReference(com.solgases.infrastructure.persistence.entity.ProductJpaEntity.class, productId),
                        BigDecimal.ZERO));
        inventory.applyQuantity(quantity);
        try {
            return InventoryPersistenceMapper.toDomain(inventoryRepository.saveAndFlush(inventory));
        } catch (DataIntegrityViolationException | ObjectOptimisticLockingFailureException ex) {
            throw concurrencyConflict(productId);
        }
    }

    @Override
    public com.solgases.domain.model.InventoryMovement saveMovement(Long productId, MovementType type,
            MovementDirection direction, BigDecimal quantity, String reason, String responsibleUser) {
        var product = entityManager.getReference(com.solgases.infrastructure.persistence.entity.ProductJpaEntity.class, productId);
        InventoryMovementJpaEntity movement = switch (type) {
            case ENTRY -> InventoryMovementJpaEntity.entry(product, quantity, reason, responsibleUser);
            case EXIT -> InventoryMovementJpaEntity.exit(product, quantity, reason, responsibleUser);
            case ADJUSTMENT -> InventoryMovementJpaEntity.adjustment(product, direction, quantity, reason, responsibleUser);
        };
        return InventoryPersistenceMapper.toDomain(movementRepository.save(movement));
    }

    private ConflictException concurrencyConflict(Long productId) {
        return new ConflictException(
                "Concurrent inventory update detected for product " + productId + "; please retry");
    }
}
