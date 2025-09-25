package com.example.ecommerce.catalog.web.exception.productvariant;

import com.example.ecommerce.catalog.web.exception.CatalogException;

/**
 * Exception thrown when there is insufficient stock for an operation.
 */
public class InsufficientStockException extends CatalogException {

    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(String message, Throwable cause) {
        super(message, cause);
    }
}