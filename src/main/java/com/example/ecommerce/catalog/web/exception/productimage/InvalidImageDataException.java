package com.example.ecommerce.catalog.web.exception.productimage;

public class InvalidImageDataException extends RuntimeException {
    public InvalidImageDataException(String message) {
        super(message);
    }

    public InvalidImageDataException(String message, Throwable cause) {
        super(message, cause);
    }
}