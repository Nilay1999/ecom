package com.example.ecommerce.catalog.web.exception.product;

import com.example.ecommerce.catalog.web.exception.CatalogException;

/**
 * Exception thrown when a requested product cannot be found.
 */
public class ProductNotFoundException extends CatalogException {

    public ProductNotFoundException(String message) {
        super(message);
    }

    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}