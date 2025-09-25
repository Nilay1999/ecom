package com.example.ecommerce.catalog.web.exception.productvariant;

import com.example.ecommerce.catalog.web.exception.CatalogException;

/**
 * Exception thrown when a requested product variant cannot be found.
 */
public class ProductVariantNotFoundException extends CatalogException {

    public ProductVariantNotFoundException(String message) {
        super(message);
    }

    public ProductVariantNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}