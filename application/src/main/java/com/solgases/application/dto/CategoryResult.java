package com.solgases.application.dto;

import com.solgases.domain.model.Category;

public record CategoryResult(Long id, String name, boolean active) {

    public static CategoryResult from(Category category) {
        return new CategoryResult(category.id(), category.name(), category.active());
    }
}
