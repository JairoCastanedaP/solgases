package com.solgases.inventory.dto;

import com.solgases.inventory.entity.Inventory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/** Body of an entry or an exit; the movement type is implied by the endpoint. */
@Schema(description = "Data to register a stock entry or exit. The type is implied by the endpoint.")
public record InventoryMovementRequest(

        @Schema(description = "Quantity to move. Always positive.", example = "10.000",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Positive
        @Digits(integer = Inventory.QUANTITY_INTEGER_DIGITS, fraction = Inventory.QUANTITY_SCALE)
        BigDecimal quantity,

        @Schema(description = "Reason for the movement", example = "Compra a proveedor",
                requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 255)
        @NotBlank
        @Size(max = 255)
        String reason,

        @Schema(description = "Identifier of the person responsible for the movement", example = "jcastaneda",
                requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 255)
        @NotBlank
        @Size(max = 255)
        String responsibleUser) {
}
