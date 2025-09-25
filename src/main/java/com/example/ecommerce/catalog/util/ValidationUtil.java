package com.example.ecommerce.catalog.util;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * Utility class for common validation operations across the catalog domain.
 * Provides reusable validation helpers for business rules and data format
 * validation.
 */
public final class ValidationUtil {

    // Common regex patterns
    private static final Pattern ALPHANUMERIC_WITH_SPACES_HYPHENS_UNDERSCORES = Pattern
            .compile("^[a-zA-Z0-9\\s\\-_]+$");

    private static final Pattern ALPHANUMERIC_WITH_SPACES_HYPHENS_UNDERSCORES_AMPERSANDS = Pattern
            .compile("^[a-zA-Z0-9\\s\\-_&]+$");

    private static final Pattern SKU_PATTERN = Pattern.compile("^[a-zA-Z0-9\\-_]+$");

    private static final Pattern SLUG_PATTERN = Pattern.compile("^[a-z0-9\\-]+$");

    private static final Pattern IMAGE_FORMAT_PATTERN = Pattern.compile(".*\\.(jpg|jpeg|png|gif|webp|svg)(\\?.*)?$",
            Pattern.CASE_INSENSITIVE);

    private ValidationUtil() {
        // Utility class - prevent instantiation
    }

    // String validation utilities

    /**
     * Validates that a string is not null or empty after trimming
     */
    public static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }

    /**
     * Validates string length constraints
     */
    public static void requireMaxLength(String value, int maxLength, String fieldName) {
        if (value != null && value.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " cannot exceed " + maxLength + " characters");
        }
    }

    /**
     * Validates string length constraints with minimum length
     */
    public static void requireLengthBetween(String value, int minLength, int maxLength, String fieldName) {
        if (value != null) {
            if (value.length() < minLength) {
                throw new IllegalArgumentException(fieldName + " must be at least " + minLength + " characters");
            }
            if (value.length() > maxLength) {
                throw new IllegalArgumentException(fieldName + " cannot exceed " + maxLength + " characters");
            }
        }
    }

    /**
     * Validates that a string matches alphanumeric characters with spaces, hyphens,
     * and underscores
     */
    public static void requireAlphanumericWithSpacesHyphensUnderscores(String value, String fieldName) {
        if (value != null && !ALPHANUMERIC_WITH_SPACES_HYPHENS_UNDERSCORES.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    fieldName
                            + " contains invalid characters. Only alphanumeric characters, spaces, hyphens, and underscores are allowed");
        }
    }

    /**
     * Validates that a string matches alphanumeric characters with spaces, hyphens,
     * underscores, and ampersands
     */
    public static void requireAlphanumericWithSpacesHyphensUnderscoresAmpersands(String value, String fieldName) {
        if (value != null && !ALPHANUMERIC_WITH_SPACES_HYPHENS_UNDERSCORES_AMPERSANDS.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    fieldName
                            + " contains invalid characters. Only alphanumeric characters, spaces, hyphens, underscores, and ampersands are allowed");
        }
    }

    /**
     * Validates SKU format (alphanumeric with hyphens and underscores only)
     */
    public static void requireValidSku(String sku, String fieldName) {
        if (sku != null && !sku.trim().isEmpty() && !SKU_PATTERN.matcher(sku).matches()) {
            throw new IllegalArgumentException(
                    fieldName
                            + " contains invalid characters. Only alphanumeric characters, hyphens, and underscores are allowed");
        }
    }

    /**
     * Validates slug format (lowercase alphanumeric with hyphens only)
     */
    public static void requireValidSlug(String slug, String fieldName) {
        if (slug != null && !SLUG_PATTERN.matcher(slug).matches()) {
            throw new IllegalArgumentException(
                    fieldName + " must contain only lowercase letters, numbers, and hyphens");
        }

        if (slug != null && (slug.startsWith("-") || slug.endsWith("-"))) {
            throw new IllegalArgumentException(fieldName + " cannot start or end with a hyphen");
        }

        if (slug != null && slug.contains("--")) {
            throw new IllegalArgumentException(fieldName + " cannot contain consecutive hyphens");
        }
    }

    // Numeric validation utilities

    /**
     * Validates that a BigDecimal is positive (greater than zero)
     */
    public static void requirePositive(BigDecimal value, String fieldName) {
        if (value != null && value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(fieldName + " must be greater than zero");
        }
    }

    /**
     * Validates that a BigDecimal is non-negative (greater than or equal to zero)
     */
    public static void requireNonNegative(BigDecimal value, String fieldName) {
        if (value != null && value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative");
        }
    }

    /**
     * Validates that a long value is non-negative
     */
    public static void requireNonNegative(long value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative");
        }
    }

    /**
     * Validates that an integer value is non-negative
     */
    public static void requireNonNegative(int value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative");
        }
    }

    /**
     * Validates BigDecimal scale (decimal places)
     */
    public static void requireMaxScale(BigDecimal value, int maxScale, String fieldName) {
        if (value != null && value.scale() > maxScale) {
            throw new IllegalArgumentException(fieldName + " cannot have more than " + maxScale + " decimal places");
        }
    }

    /**
     * Validates that a BigDecimal is within a specific range
     */
    public static void requireRange(BigDecimal value, BigDecimal min, BigDecimal max, String fieldName) {
        if (value != null) {
            if (min != null && value.compareTo(min) < 0) {
                throw new IllegalArgumentException(fieldName + " must be at least " + min);
            }
            if (max != null && value.compareTo(max) > 0) {
                throw new IllegalArgumentException(fieldName + " cannot exceed " + max);
            }
        }
    }

    /**
     * Validates that a long value is within a specific range
     */
    public static void requireRange(long value, long min, long max, String fieldName) {
        if (value < min) {
            throw new IllegalArgumentException(fieldName + " must be at least " + min);
        }
        if (value > max) {
            throw new IllegalArgumentException(fieldName + " cannot exceed " + max);
        }
    }

    // URL and format validation utilities

    /**
     * Validates that a string is a valid URL
     */
    public static void requireValidUrl(String url, String fieldName) {
        if (url != null) {
            try {
                new URL(url);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Invalid " + fieldName + " format: " + url, e);
            }
        }
    }

    /**
     * Validates that a URL points to a supported image format
     */
    public static void requireValidImageUrl(String imageUrl, String fieldName) {
        requireValidUrl(imageUrl, fieldName);

        if (imageUrl != null && !IMAGE_FORMAT_PATTERN.matcher(imageUrl).matches()) {
            throw new IllegalArgumentException(
                    "Unsupported " + fieldName + " format. Supported formats: jpg, jpeg, png, gif, webp, svg");
        }
    }

    // Business rule validation utilities

    /**
     * Validates product rating (0-5 range with max 2 decimal places)
     */
    public static void requireValidRating(BigDecimal rating, String fieldName) {
        if (rating != null) {
            requireRange(rating, BigDecimal.ZERO, BigDecimal.valueOf(5), fieldName);
            requireMaxScale(rating, 2, fieldName);
        }
    }

    /**
     * Validates price (positive with max 2 decimal places)
     */
    public static void requireValidPrice(BigDecimal price, String fieldName) {
        if (price != null) {
            requirePositive(price, fieldName);
            requireMaxScale(price, 2, fieldName);
        }
    }

    /**
     * Validates weight (positive with max 2 decimal places)
     */
    public static void requireValidWeight(BigDecimal weight, String fieldName) {
        if (weight != null) {
            requirePositive(weight, fieldName);
            requireMaxScale(weight, 2, fieldName);
        }
    }

    /**
     * Validates stock quantity (non-negative)
     */
    public static void requireValidStockQuantity(long stockQuantity, String fieldName) {
        requireNonNegative(stockQuantity, fieldName);
    }

    /**
     * Validates display order (non-negative)
     */
    public static void requireValidDisplayOrder(Integer displayOrder, String fieldName) {
        if (displayOrder != null) {
            requireNonNegative(displayOrder, fieldName);
        }
    }

    // Constraint validation utilities

    /**
     * Validates that sufficient stock is available for removal
     */
    public static void requireSufficientStock(long currentStock, long requestedRemoval, String operation) {
        if (requestedRemoval > currentStock) {
            throw new IllegalArgumentException(
                    "Cannot " + operation + " more stock than available. Available: " + currentStock +
                            ", Requested: " + requestedRemoval);
        }
    }

    /**
     * Validates that an object is not null
     */
    public static void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }

    // Composite validation utilities

    /**
     * Validates a complete product name (non-blank, max length, valid characters)
     */
    public static void validateProductName(String productName) {
        requireNonBlank(productName, "Product name");
        requireMaxLength(productName, 255, "Product name");
    }

    /**
     * Validates a complete brand name (non-blank, max length)
     */
    public static void validateBrandName(String brandName) {
        requireNonBlank(brandName, "Brand name");
        requireMaxLength(brandName, 100, "Brand name");
    }

    /**
     * Validates a complete category name (non-blank, max length, valid characters)
     */
    public static void validateCategoryName(String categoryName) {
        requireNonBlank(categoryName, "Category name");
        requireMaxLength(categoryName, 100, "Category name");
        requireAlphanumericWithSpacesHyphensUnderscoresAmpersands(categoryName, "Category name");
    }

    /**
     * Validates a complete variant name (non-blank, max length, valid characters)
     */
    public static void validateVariantName(String variantName) {
        requireNonBlank(variantName, "Variant name");
        requireMaxLength(variantName, 100, "Variant name");
        requireAlphanumericWithSpacesHyphensUnderscores(variantName, "Variant name");
    }

    /**
     * Validates a complete image URL (non-blank, max length, valid URL, valid
     * format)
     */
    public static void validateImageUrl(String imageUrl) {
        requireNonBlank(imageUrl, "Image URL");
        requireMaxLength(imageUrl, 500, "Image URL");
        requireValidImageUrl(imageUrl, "Image URL");
    }

    /**
     * Validates a complete category slug (non-blank, max length, valid format)
     */
    public static void validateCategorySlug(String slug) {
        requireNonBlank(slug, "Category slug");
        requireMaxLength(slug, 200, "Category slug");
        requireValidSlug(slug, "Category slug");
    }
}