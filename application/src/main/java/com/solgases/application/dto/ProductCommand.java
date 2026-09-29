package com.solgases.application.dto;

import java.math.BigDecimal;

public record ProductCommand(String sku, String name, String description, String brand, String reference,
        BigDecimal price, Long categoryId, Long unitOfMeasureId) {
}
