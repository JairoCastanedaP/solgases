package com.solgases.application.port.in;

import com.solgases.application.dto.CategoryCommand;
import com.solgases.application.dto.CategoryResult;

public interface CreateCategoryUseCase {

    CategoryResult execute(CategoryCommand command);
}
