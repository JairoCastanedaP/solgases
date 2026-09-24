package com.solgases.inventory.service;

import com.solgases.exception.ResourceConflictException;
import com.solgases.exception.ResourceNotFoundException;
import com.solgases.inventory.dto.InventoryAdjustmentRequest;
import com.solgases.inventory.dto.InventoryMovementRequest;
import com.solgases.inventory.dto.InventoryMovementResponse;
import com.solgases.inventory.dto.InventoryResponse;
import com.solgases.inventory.entity.Inventory;
import com.solgases.inventory.entity.InventoryMovement;
import com.solgases.inventory.entity.MovementDirection;
import com.solgases.inventory.repository.InventoryMovementRepository;
import com.solgases.inventory.repository.InventoryRepository;
import com.solgases.product.entity.Product;
import com.solgases.product.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final ProductRepository productRepository;

    public InventoryService(InventoryRepository inventoryRepository,
            InventoryMovementRepository inventoryMovementRepository, ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryMovementRepository = inventoryMovementRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public InventoryResponse getCurrentQuantity(Long productId) {
        requireProduct(productId);
        return inventoryRepository.findByProductId(productId)
                .map(InventoryResponse::from)
                .orElseGet(() -> InventoryResponse.zero(productId));
    }

    @Transactional(readOnly = true)
    public List<InventoryMovementResponse> getMovements(Long productId) {
        requireProduct(productId);
        return inventoryMovementRepository.findAllByProductIdOrderByMovementDateDesc(productId).stream()
                .map(InventoryMovementResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public InventoryMovementResponse getMovement(Long productId, Long movementId) {
        requireProduct(productId);
        InventoryMovement movement = inventoryMovementRepository.findByIdAndProductId(movementId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Movement not found with id " + movementId + " for product " + productId));
        return InventoryMovementResponse.from(movement);
    }

    @Transactional
    public InventoryMovementResponse registerEntry(Long productId, InventoryMovementRequest request) {
        Product product = requireActiveProduct(productId);
        InventoryMovement movement = InventoryMovement.entry(product, request.quantity(), request.reason(), request.responsibleUser());
        return applyMovement(product, MovementDirection.INCREASE, request.quantity(), movement);
    }

    @Transactional
    public InventoryMovementResponse registerExit(Long productId, InventoryMovementRequest request) {
        Product product = requireActiveProduct(productId);
        InventoryMovement movement = InventoryMovement.exit(product, request.quantity(), request.reason(), request.responsibleUser());
        return applyMovement(product, MovementDirection.DECREASE, request.quantity(), movement);
    }

    @Transactional
    public InventoryMovementResponse registerAdjustment(Long productId, InventoryAdjustmentRequest request) {
        Product product = requireActiveProduct(productId);
        InventoryMovement movement = InventoryMovement.adjustment(
                product, request.direction(), request.quantity(), request.reason(), request.responsibleUser());
        return applyMovement(product, request.direction(), request.quantity(), movement);
    }

    /**
     * Applies a movement's effect on the product's stock and persists both the (possibly newly
     * created) {@link Inventory} row and the {@link InventoryMovement}, as a single unit: if the
     * stock check fails or a concurrency conflict is detected, nothing is persisted.
     */
    private InventoryMovementResponse applyMovement(Product product, MovementDirection direction,
            BigDecimal quantity, InventoryMovement movement) {
        Inventory inventory = inventoryRepository.findByProductId(product.getId())
                .orElseGet(() -> new Inventory(product, BigDecimal.ZERO));

        BigDecimal newQuantity = direction == MovementDirection.INCREASE
                ? inventory.getCurrentQuantity().add(quantity)
                : inventory.getCurrentQuantity().subtract(quantity);

        if (newQuantity.signum() < 0) {
            throw new ResourceConflictException(
                    "Insufficient stock for product " + product.getId() + ": available " + inventory.getCurrentQuantity()
                            + ", requested " + quantity);
        }

        inventory.applyQuantity(newQuantity);
        saveInventoryOrConflict(inventory, product.getId());

        InventoryMovement savedMovement = inventoryMovementRepository.save(movement);
        log.info("Movement {} ({}/{}) registered for product {}", savedMovement.getId(),
                savedMovement.getType(), savedMovement.getDirection(), product.getId());
        return InventoryMovementResponse.from(savedMovement);
    }

    /**
     * Flushes immediately so that a concurrency conflict surfaces here and can be reported to the
     * caller: a violation of {@code uk_inventory_product} when the first movement for a product is
     * being registered concurrently by two requests, or an optimistic-locking failure when an
     * existing Inventory row was modified concurrently.
     */
    private void saveInventoryOrConflict(Inventory inventory, Long productId) {
        try {
            inventoryRepository.saveAndFlush(inventory);
        } catch (DataIntegrityViolationException | ObjectOptimisticLockingFailureException ex) {
            throw new ResourceConflictException(
                    "Concurrent inventory update detected for product " + productId + "; please retry");
        }
    }

    private Product requireProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + productId));
    }

    private Product requireActiveProduct(Long productId) {
        Product product = requireProduct(productId);
        if (!product.isActive()) {
            throw new ResourceConflictException("Product with id " + productId + " is not active");
        }
        return product;
    }
}
