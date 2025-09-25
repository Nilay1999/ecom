package com.example.ecommerce.catalog.web.exception.productvariant;

import com.example.ecommerce.catalog.web.exception.CatalogException;

/**
 * Exception thrown when attempting to create a product variant that already
 * exists.
 */
public class DuplicateVariantException extends CatalogException {

    public DuplicateVariantException(String message) {
        super(message);
    }

    public DuplicateVariantException(String message, Throwable cause) {
        super(message, cause);
    }
}