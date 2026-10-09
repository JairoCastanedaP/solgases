package com.solgases.application.port.in;

import com.solgases.application.dto.CategoryResult;

public interface DeactivateCategoryUseCase {

    CategoryResult execute(Long id);
}
