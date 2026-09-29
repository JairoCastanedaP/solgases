package com.solgases.application.port.in;

import com.solgases.application.dto.CategoryResult;

public interface GetCategoryByIdUseCase {

    CategoryResult execute(Long id);
}
