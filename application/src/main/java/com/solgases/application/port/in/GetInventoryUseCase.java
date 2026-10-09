package com.solgases.application.port.in;

import com.solgases.application.dto.InventoryResult;

public interface GetInventoryUseCase { InventoryResult execute(Long productId); }
