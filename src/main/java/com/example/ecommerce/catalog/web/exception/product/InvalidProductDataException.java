package com.example.ecommerce.catalog.web.exception.product;

import com.example.ecommerce.catalog.web.exception.CatalogException;

/**
 * Exception thrown when product data validation fails.
 * This includes business rule violations and data format issues.
 */
public class InvalidProductDataException extends CatalogException {

    public InvalidProductDataException(String message) {
        super(message);
    }

    public InvalidProductDataException(String message, Throwable cause) {
        super(message, cause);
    }
}