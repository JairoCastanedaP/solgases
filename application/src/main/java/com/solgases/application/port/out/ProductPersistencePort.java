package com.solgases.application.port.out;

import com.solgases.domain.model.Category;
import com.solgases.application.dto.ProductQuery;
import com.solgases.domain.model.Product;
import com.solgases.domain.model.UnitOfMeasure;
import java.util.List;
import java.util.Optional;

public interface ProductPersistencePort {
    Optional<Product> findById(Long id);
    List<Product> findAll(ProductQuery query);
    Optional<Category> findCategoryById(Long id);
    Optional<UnitOfMeasure> findUnitOfMeasureById(Long id);
    boolean existsBySku(String sku);
    boolean existsBySkuAndIdNot(String sku, Long id);
    Product saveNew(Product product);
    Product saveChanges(Product product);
}
