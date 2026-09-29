package com.solgases.application.dto;

import java.math.BigDecimal;

public record InventoryMovementCommand(BigDecimal quantity, String reason, String responsibleUser) { }
