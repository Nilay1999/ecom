package com.example.ecommerce.catalog.web.exception.category;

import com.example.ecommerce.catalog.web.exception.CatalogException;

/**
 * Exception thrown when a requested category cannot be found.
 */
public class CategoryNotFoundException extends CatalogException {

    public CategoryNotFoundException(String message) {
        super(message);
    }

    public CategoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
