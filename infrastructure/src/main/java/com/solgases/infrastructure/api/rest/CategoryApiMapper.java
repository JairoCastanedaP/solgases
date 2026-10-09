package com.solgases.infrastructure.api.rest;

import com.solgases.application.dto.CategoryCommand;
import com.solgases.application.dto.CategoryResult;
import com.solgases.infrastructure.api.dto.CategoryRequest;
import com.solgases.infrastructure.api.dto.CategoryResponse;

public final class CategoryApiMapper {
    private CategoryApiMapper() {}
    public static CategoryCommand toCommand(CategoryRequest request) { return new CategoryCommand(request.name()); }
    public static CategoryResponse toResponse(CategoryResult result) {
        return new CategoryResponse(result.id(), result.name(), result.active());
    }
}
