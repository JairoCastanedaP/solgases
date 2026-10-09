package com.solgases.infrastructure.api.rest;

import com.solgases.infrastructure.api.dto.CategoryResponse;
import com.solgases.application.dto.ProductCommand;
import com.solgases.application.dto.ProductQuery;
import com.solgases.application.dto.ProductResult;
import com.solgases.infrastructure.api.dto.ProductRequest;
import com.solgases.infrastructure.api.dto.ProductResponse;
import com.solgases.infrastructure.api.dto.UnitOfMeasureResponse;

public final class ProductApiMapper {
    private ProductApiMapper() {}
    public static ProductCommand toCommand(ProductRequest request) {
        return new ProductCommand(request.sku(), request.name(), request.description(), request.brand(),
                request.reference(), request.price(), request.categoryId(), request.unitOfMeasureId());
    }
    public static ProductQuery toQuery(String name, Long categoryId, Boolean active) {
        return new ProductQuery(name, categoryId, active);
    }
    public static ProductResponse toResponse(ProductResult result) {
        return new ProductResponse(result.id(), result.sku(), result.name(), result.description(), result.brand(),
                result.reference(), result.price(), result.active(),
                new CategoryResponse(result.category().id(), result.category().name(), result.category().active()),
                new UnitOfMeasureResponse(result.unitOfMeasure().id(), result.unitOfMeasure().code(),
                        result.unitOfMeasure().name(), result.unitOfMeasure().active()));
    }
}
