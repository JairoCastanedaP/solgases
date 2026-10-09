package com.solgases.application.port.in;

import com.solgases.application.dto.ProductResult;

public interface GetProductByIdUseCase { ProductResult execute(Long id); }
