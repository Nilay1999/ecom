package com.example.ecommerce.catalog.web.exception.productvariant;

import com.example.ecommerce.catalog.web.exception.CatalogException;

/**
 * Exception thrown when product variant data validation fails.
 */
public class InvalidVariantDataException extends CatalogException {

    public InvalidVariantDataException(String message) {
        super(message);
    }

    public InvalidVariantDataException(String message, Throwable cause) {
        super(message, cause);
    }
}