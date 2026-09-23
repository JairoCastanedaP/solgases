package com.solgases.product.entity;

import com.solgases.category.entity.Category;
import com.solgases.unitofmeasure.entity.UnitOfMeasure;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;

@Entity
@Table(
        name = "tbl_product",
        uniqueConstraints = @UniqueConstraint(name = "uk_product_sku", columnNames = "sku"))
public class Product {

    public static final int SKU_MAX_LENGTH = 50;
    public static final int NAME_MAX_LENGTH = 100;
    public static final int DESCRIPTION_MAX_LENGTH = 255;
    public static final int BRAND_MAX_LENGTH = 255;
    public static final int REFERENCE_MAX_LENGTH = 255;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sku", nullable = false, length = SKU_MAX_LENGTH)
    private String sku;

    @Column(name = "name", nullable = false, length = NAME_MAX_LENGTH)
    private String name;

    @Column(name = "description", length = DESCRIPTION_MAX_LENGTH)
    private String description;

    @Column(name = "brand", length = BRAND_MAX_LENGTH)
    private String brand;

    @Column(name = "reference", length = REFERENCE_MAX_LENGTH)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_of_measure_id", nullable = false)
    private UnitOfMeasure unitOfMeasure;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "active", nullable = false)
    private boolean active;

    protected Product() {
        // Required by JPA
    }

    public Product(String sku, String name, String description, String brand, String reference,
            Category category, UnitOfMeasure unitOfMeasure, BigDecimal price) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.reference = reference;
        this.category = category;
        this.unitOfMeasure = unitOfMeasure;
        this.price = price;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getBrand() {
        return brand;
    }

    public String getReference() {
        return reference;
    }

    public Category getCategory() {
        return category;
    }

    public UnitOfMeasure getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean isActive() {
        return active;
    }

    public void replaceDetails(String sku, String name, String description, String brand, String reference,
            Category category, UnitOfMeasure unitOfMeasure, BigDecimal price) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.reference = reference;
        this.category = category;
        this.unitOfMeasure = unitOfMeasure;
        this.price = price;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
