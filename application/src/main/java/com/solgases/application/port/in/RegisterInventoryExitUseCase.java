package com.solgases.application.port.in;

import com.solgases.application.dto.InventoryMovementCommand;
import com.solgases.application.dto.InventoryMovementResult;

public interface RegisterInventoryExitUseCase { InventoryMovementResult execute(Long productId, InventoryMovementCommand command); }
