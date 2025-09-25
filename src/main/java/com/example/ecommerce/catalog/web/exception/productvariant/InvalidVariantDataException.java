package com.example.ecommerce.catalog.web.exception.productvariant;

public class InvalidVariantDataException extends RuntimeException {
    public InvalidVariantDataException(String message) {
        super(message);
    }

    public InvalidVariantDataException(String message, Throwable cause) {
        super(message, cause);
    }
}