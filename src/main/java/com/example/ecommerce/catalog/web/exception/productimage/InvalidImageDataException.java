package com.example.ecommerce.catalog.web.exception.productimage;

import com.example.ecommerce.catalog.web.exception.CatalogException;

/**
 * Exception thrown when product image data validation fails.
 */
public class InvalidImageDataException extends CatalogException {

    public InvalidImageDataException(String message) {
        super(message);
    }

    public InvalidImageDataException(String message, Throwable cause) {
        super(message, cause);
    }
}