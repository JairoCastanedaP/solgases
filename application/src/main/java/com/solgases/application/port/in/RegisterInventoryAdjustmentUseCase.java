package com.solgases.application.port.in;

import com.solgases.application.dto.InventoryAdjustmentCommand;
import com.solgases.application.dto.InventoryMovementResult;

public interface RegisterInventoryAdjustmentUseCase { InventoryMovementResult execute(Long productId, InventoryAdjustmentCommand command); }
