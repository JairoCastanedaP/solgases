package com.solgases.application.port.in;

import com.solgases.application.dto.InventoryMovementResult;

public interface GetInventoryMovementUseCase { InventoryMovementResult execute(Long productId, Long movementId); }
