package com.example.ecommerce.catalog.web.exception;

import com.example.ecommerce.catalog.web.exception.category.CategoryNotFoundException;
import com.example.ecommerce.catalog.web.exception.category.DuplicateCategoryException;
import com.example.ecommerce.catalog.web.exception.product.DuplicateProductException;
import com.example.ecommerce.catalog.web.exception.product.InvalidProductDataException;
import com.example.ecommerce.catalog.web.exception.product.ProductNotFoundException;
import com.example.ecommerce.catalog.web.exception.productimage.InvalidImageDataException;
import com.example.ecommerce.catalog.web.exception.productimage.PrimaryImageConstraintException;
import com.example.ecommerce.catalog.web.exception.productimage.ProductImageNotFoundException;
import com.example.ecommerce.catalog.web.exception.productvariant.DuplicateVariantException;
import com.example.ecommerce.catalog.web.exception.productvariant.InsufficientStockException;
import com.example.ecommerce.catalog.web.exception.productvariant.InvalidVariantDataException;
import com.example.ecommerce.catalog.web.exception.productvariant.ProductVariantNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Product-related exceptions
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException ex,
            WebRequest request) {
        logger.warn("Product not found: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "PRODUCT_NOT_FOUND",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(InvalidProductDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidProductDataException(InvalidProductDataException ex,
            WebRequest request) {
        logger.warn("Invalid product data: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "INVALID_PRODUCT_DATA",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(DuplicateProductException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateProductException(DuplicateProductException ex,
            WebRequest request) {
        logger.warn("Duplicate product: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "DUPLICATE_PRODUCT",
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    // Category-related exceptions
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCategoryNotFoundException(CategoryNotFoundException ex,
            WebRequest request) {
        logger.warn("Category not found: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "CATEGORY_NOT_FOUND",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(DuplicateCategoryException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateCategoryException(DuplicateCategoryException ex,
            WebRequest request) {
        logger.warn("Duplicate category: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "DUPLICATE_CATEGORY",
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    // Product Image-related exceptions
    @ExceptionHandler(ProductImageNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductImageNotFoundException(ProductImageNotFoundException ex,
            WebRequest request) {
        logger.warn("Product image not found: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "PRODUCT_IMAGE_NOT_FOUND",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(InvalidImageDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidImageDataException(InvalidImageDataException ex,
            WebRequest request) {
        logger.warn("Invalid image data: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "INVALID_IMAGE_DATA",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(PrimaryImageConstraintException.class)
    public ResponseEntity<ErrorResponse> handlePrimaryImageConstraintException(PrimaryImageConstraintException ex,
            WebRequest request) {
        logger.warn("Primary image constraint violation: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "PRIMARY_IMAGE_CONSTRAINT_VIOLATION",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Product Variant-related exceptions
    @ExceptionHandler(ProductVariantNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductVariantNotFoundException(ProductVariantNotFoundException ex,
            WebRequest request) {
        logger.warn("Product variant not found: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "PRODUCT_VARIANT_NOT_FOUND",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(InvalidVariantDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidVariantDataException(InvalidVariantDataException ex,
            WebRequest request) {
        logger.warn("Invalid variant data: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "INVALID_VARIANT_DATA",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStockException(InsufficientStockException ex,
            WebRequest request) {
        logger.warn("Insufficient stock: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "INSUFFICIENT_STOCK",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(DuplicateVariantException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateVariantException(DuplicateVariantException ex,
            WebRequest request) {
        logger.warn("Duplicate variant: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "DUPLICATE_VARIANT",
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    // Validation exception
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex, WebRequest request) {
        logger.warn("Validation error: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "VALIDATION_ERROR",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false).replace("uri=", ""),
                ex.getFieldErrors());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // General CatalogException handler (fallback for any custom exceptions not
    // specifically handled)
    @ExceptionHandler(CatalogException.class)
    public ResponseEntity<ErrorResponse> handleCatalogException(CatalogException ex, WebRequest request) {
        logger.error("Catalog exception: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(
                "CATALOG_ERROR",
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    // Spring validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex,
            WebRequest request) {
        logger.warn("Method argument validation failed: {}", ex.getMessage());

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        ErrorResponse errorResponse = new ErrorResponse(
                "VALIDATION_ERROR",
                "Validation failed for one or more fields",
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false).replace("uri=", ""),
                fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Data integrity violations
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex,
            WebRequest request) {
        logger.warn("Data integrity violation: {}", ex.getMessage());

        String message = ex.getMessage();
        String errorCode = "DATA_INTEGRITY_VIOLATION";
        String errorMessage = "Data integrity violation occurred";

        if (message != null && message.contains("categories_slug_key")) {
            errorCode = "DUPLICATE_CATEGORY_SLUG";
            errorMessage = "A category with this slug already exists";
        } else if (message != null && message.contains("categories_name_key")) {
            errorCode = "DUPLICATE_CATEGORY_NAME";
            errorMessage = "A category with this name already exists";
        } else if (message != null) {
            errorMessage = "Data integrity violation: " + ex.getMostSpecificCause().getMessage();
        }

        ErrorResponse errorResponse = new ErrorResponse(
                errorCode,
                errorMessage,
                HttpStatus.CONFLICT.value(),
                request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    // Illegal argument exceptions
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex,
            WebRequest request) {
        logger.warn("Illegal argument: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "ILLEGAL_ARGUMENT",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Generic exception handler (fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
