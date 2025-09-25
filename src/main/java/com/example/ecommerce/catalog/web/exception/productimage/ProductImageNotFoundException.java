package com.example.ecommerce.catalog.web.exception.productimage;

import com.example.ecommerce.catalog.web.exception.CatalogException;

/**
 * Exception thrown when a requested product image cannot be found.
 */
public class ProductImageNotFoundException extends CatalogException {

    public ProductImageNotFoundException(String message) {
        super(message);
    }

    public ProductImageNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}