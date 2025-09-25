package com.example.ecommerce.catalog.web.exception.productvariant;

public class DuplicateVariantException extends RuntimeException {
    public DuplicateVariantException(String message) {
        super(message);
    }
}