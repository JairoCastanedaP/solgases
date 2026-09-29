package com.solgases.application.port.in;

import com.solgases.application.dto.CategoryResult;
import java.util.List;

public interface ListCategoriesUseCase {

    List<CategoryResult> execute();
}
