package com.solgases.application.port.in;

import com.solgases.application.dto.InventoryMovementResult;
import java.util.List;

public interface ListInventoryMovementsUseCase { List<InventoryMovementResult> execute(Long productId); }
