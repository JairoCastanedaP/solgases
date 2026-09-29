package com.solgases.application.port.in;

import com.solgases.application.dto.ProductCommand;
import com.solgases.application.dto.ProductResult;

public interface CreateProductUseCase { ProductResult execute(ProductCommand command); }
