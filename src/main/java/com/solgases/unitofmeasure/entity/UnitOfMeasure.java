package com.solgases.unitofmeasure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "tbl_unit_of_measure",
        uniqueConstraints = @UniqueConstraint(name = "uk_unit_of_measure_code", columnNames = "code"))
public class UnitOfMeasure {

    public static final int CODE_MAX_LENGTH = 20;
    public static final int NAME_MAX_LENGTH = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, length = CODE_MAX_LENGTH)
    private String code;

    @Column(name = "name", nullable = false, length = NAME_MAX_LENGTH)
    private String name;

    @Column(name = "active", nullable = false)
    private boolean active;

    protected UnitOfMeasure() {
        // Required by JPA
    }

    public UnitOfMeasure(String code, String name) {
        this.code = code;
        this.name = name;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public void update(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
