package com.solgases.application.exception;

public class DuplicateProductSkuException extends RuntimeException {
    public DuplicateProductSkuException(String sku) { super("A product with the SKU '" + sku + "' already exists"); }
}
