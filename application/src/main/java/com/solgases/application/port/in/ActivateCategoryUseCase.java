package com.solgases.application.port.in;

import com.solgases.application.dto.CategoryResult;

public interface ActivateCategoryUseCase {

    CategoryResult execute(Long id);
}
