package com.solgases.unitofmeasure.dto;

import com.solgases.unitofmeasure.entity.UnitOfMeasure;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A unit of measure")
public record UnitOfMeasureResponse(

        @Schema(description = "Generated identifier", example = "1")
        Long id,

        @Schema(description = "Short code", example = "UN")
        String code,

        @Schema(description = "Unit name", example = "Unidad")
        String name,

        @Schema(description = "Whether the unit is active", example = "true")
        boolean active) {

    public static UnitOfMeasureResponse from(UnitOfMeasure unitOfMeasure) {
        return new UnitOfMeasureResponse(
                unitOfMeasure.getId(), unitOfMeasure.getCode(), unitOfMeasure.getName(), unitOfMeasure.isActive());
    }
}
