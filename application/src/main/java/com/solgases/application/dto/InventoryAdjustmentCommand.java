package com.solgases.application.dto;

import com.solgases.domain.model.MovementDirection;
import java.math.BigDecimal;

public record InventoryAdjustmentCommand(BigDecimal quantity, MovementDirection direction, String reason,
        String responsibleUser) { }
