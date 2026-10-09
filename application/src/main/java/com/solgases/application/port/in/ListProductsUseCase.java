package com.solgases.application.port.in;

import com.solgases.application.dto.ProductQuery;
import com.solgases.application.dto.ProductResult;
import java.util.List;

public interface ListProductsUseCase { List<ProductResult> execute(ProductQuery query); }
