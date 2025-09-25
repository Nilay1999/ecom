package com.example.ecommerce.catalog.web.exception.category;

import com.example.ecommerce.catalog.web.exception.CatalogException;

/**
 * Exception thrown when attempting to create a category that already exists.
 */
public class DuplicateCategoryException extends CatalogException {

    public DuplicateCategoryException(String message) {
        super(message);
    }

    public DuplicateCategoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
