package com.example.ecommerce.catalog.web.exception.productimage;

import com.example.ecommerce.catalog.web.exception.CatalogException;

/**
 * Exception thrown when primary image business rules are violated.
 */
public class PrimaryImageConstraintException extends CatalogException {

    public PrimaryImageConstraintException(String message) {
        super(message);
    }

    public PrimaryImageConstraintException(String message, Throwable cause) {
        super(message, cause);
    }
}