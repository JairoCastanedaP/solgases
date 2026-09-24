package com.solgases.inventory.dto;

import com.solgases.inventory.entity.Inventory;
import com.solgases.inventory.entity.MovementDirection;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Schema(description = "Data to register a stock adjustment")
public record InventoryAdjustmentRequest(

        @Schema(description = "Quantity to move. Always positive; the effect on stock is determined by direction.",
                example = "2.000", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Positive
        @Digits(integer = Inventory.QUANTITY_INTEGER_DIGITS, fraction = Inventory.QUANTITY_SCALE)
        BigDecimal quantity,

        @Schema(description = "Whether the adjustment increases or decreases the stock",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        MovementDirection direction,

        @Schema(description = "Reason for the adjustment", example = "Conteo físico",
                requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 255)
        @NotBlank
        @Size(max = 255)
        String reason,

        @Schema(description = "Identifier of the person responsible for the adjustment", example = "jcastaneda",
                requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 255)
        @NotBlank
        @Size(max = 255)
        String responsibleUser) {
}
