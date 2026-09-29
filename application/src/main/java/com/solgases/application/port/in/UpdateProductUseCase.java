package com.solgases.application.port.in;

import com.solgases.application.dto.ProductCommand;
import com.solgases.application.dto.ProductResult;

public interface UpdateProductUseCase { ProductResult execute(Long id, ProductCommand command); }
