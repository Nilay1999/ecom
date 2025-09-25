package com.example.ecommerce.catalog.web.exception.product;

import com.example.ecommerce.catalog.web.exception.CatalogException;

/**
 * Exception thrown when attempting to create a product that already exists.
 * This typically occurs when there's a constraint violation on unique fields.
 */
public class DuplicateProductException extends CatalogException {

    public DuplicateProductException(String message) {
        super(message);
    }

    public DuplicateProductException(String message, Throwable cause) {
        super(message, cause);
    }
}