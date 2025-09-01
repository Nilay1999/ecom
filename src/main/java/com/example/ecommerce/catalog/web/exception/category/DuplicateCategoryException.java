package com.example.ecommerce.catalog.web.exception.category;


public class DuplicateCategoryException extends RuntimeException {
    public DuplicateCategoryException(String message) {
        super(message);
    }
}
