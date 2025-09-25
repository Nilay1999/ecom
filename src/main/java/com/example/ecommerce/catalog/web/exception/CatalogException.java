package com.example.ecommerce.catalog.web.exception;

/**
 * Base exception class for all catalog-related exceptions.
 * Provides a common foundation for the catalog exception hierarchy.
 */
public abstract class CatalogException extends RuntimeException {

    public CatalogException(String message) {
        super(message);
    }

    public CatalogException(String message, Throwable cause) {
        super(message, cause);
    }

    public CatalogException(Throwable cause) {
        super(cause);
    }
}