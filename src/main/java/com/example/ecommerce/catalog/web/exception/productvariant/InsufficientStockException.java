package com.example.ecommerce.catalog.web.exception.productvariant;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}