package com.example.ecommerce.catalog.web.exception.productvariant;

public class ProductVariantNotFoundException extends RuntimeException {
    public ProductVariantNotFoundException(String message) {
        super(message);
    }
}