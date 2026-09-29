package com.solgases.infrastructure.config;

import com.solgases.application.port.out.ProductPersistencePort;
import com.solgases.application.usecase.ActivateProductService;
import com.solgases.application.usecase.CreateProductService;
import com.solgases.application.usecase.DeactivateProductService;
import com.solgases.application.usecase.GetProductByIdService;
import com.solgases.application.usecase.ListProductsService;
import com.solgases.application.usecase.UpdateProductService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductUseCaseConfiguration {

    @Bean
    CreateProductService createProductService(ProductPersistencePort port) { return new CreateProductService(port); }

    @Bean
    ListProductsService listProductsService(ProductPersistencePort port) { return new ListProductsService(port); }

    @Bean
    GetProductByIdService getProductByIdService(ProductPersistencePort port) { return new GetProductByIdService(port); }

    @Bean
    UpdateProductService updateProductService(ProductPersistencePort port) { return new UpdateProductService(port); }

    @Bean
    ActivateProductService activateProductService(ProductPersistencePort port) { return new ActivateProductService(port); }

    @Bean
    DeactivateProductService deactivateProductService(ProductPersistencePort port) { return new DeactivateProductService(port); }
}
