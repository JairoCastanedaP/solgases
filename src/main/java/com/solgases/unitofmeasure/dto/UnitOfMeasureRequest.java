package com.solgases.unitofmeasure.dto;

import com.solgases.unitofmeasure.entity.UnitOfMeasure;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Editable data of a unit of measure. The id and the active flag cannot be set by the client.")
public record UnitOfMeasureRequest(

        @Schema(description = "Short code. Stored exactly as received (no trimming or normalization). Must be unique.",
                example = "UN", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = UnitOfMeasure.CODE_MAX_LENGTH)
        @NotBlank
        @Size(max = UnitOfMeasure.CODE_MAX_LENGTH)
        String code,

        @Schema(description = "Unit name. Not required to be unique.",
                example = "Unidad", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = UnitOfMeasure.NAME_MAX_LENGTH)
        @NotBlank
        @Size(max = UnitOfMeasure.NAME_MAX_LENGTH)
        String name) {
}
