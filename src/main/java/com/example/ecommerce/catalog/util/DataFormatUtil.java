package com.example.ecommerce.catalog.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Utility class for data format validation and standardization
 * across the catalog domain.
 */
public final class DataFormatUtil {

    // Common format patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+]?[1-9]\\d{1,14}$"); // Basic international phone format

    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9\\s\\-]{3,10}$"); // Generic postal code format

    // Number formatters
    private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("#,##0.00");
    private static final DecimalFormat WEIGHT_FORMAT = new DecimalFormat("#,##0.00");
    private static final DecimalFormat RATING_FORMAT = new DecimalFormat("#0.0");

    private DataFormatUtil() {
        // Utility class - prevent instantiation
    }

    // String formatting and normalization

    /**
     * Normalizes a string by trimming whitespace and converting to proper case
     */
    public static String normalizeString(String input) {
        if (input == null) {
            return null;
        }
        return input.trim();
    }

    /**
     * Normalizes a name string (trims, proper case)
     */
    public static String normalizeName(String name) {
        if (name == null) {
            return null;
        }
        return name.trim();
    }

    /**
     * Normalizes a description by trimming and removing excessive whitespace
     */
    public static String normalizeDescription(String description) {
        if (description == null) {
            return null;
        }
        return description.trim().replaceAll("\\s+", " ");
    }

    /**
     * Generates a URL-friendly slug from a string
     */
    public static String generateSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Cannot generate slug from null or empty input");
        }

        String slug = input.trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s\\-]", "") // Remove invalid characters
                .replaceAll("\\s+", "-") // Replace spaces with hyphens
                .replaceAll("-+", "-"); // Replace multiple hyphens with single hyphen

        // Remove leading/trailing hyphens
        slug = slug.replaceAll("^-+|-+$", "");

        if (slug.isEmpty()) {
            throw new IllegalArgumentException("Generated slug is empty after processing input: " + input);
        }

        return slug;
    }

    /**
     * Formats a string to title case
     */
    public static String toTitleCase(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }

        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            } else {
                c = Character.toLowerCase(c);
            }
            titleCase.append(c);
        }

        return titleCase.toString();
    }

    // Numeric formatting and validation

    /**
     * Formats a price for display with currency symbol
     */
    public static String formatPrice(BigDecimal price, Currency currency) {
        if (price == null) {
            return "N/A";
        }

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        if (currency != null) {
            currencyFormat.setCurrency(currency);
        }

        return currencyFormat.format(price);
    }

    /**
     * Formats a price for display without currency symbol
     */
    public static String formatPrice(BigDecimal price) {
        if (price == null) {
            return "0.00";
        }
        return PRICE_FORMAT.format(price);
    }

    /**
     * Formats weight for display
     */
    public static String formatWeight(BigDecimal weight, String unit) {
        if (weight == null) {
            return "N/A";
        }
        return WEIGHT_FORMAT.format(weight) + (unit != null ? " " + unit : "");
    }

    /**
     * Formats rating for display
     */
    public static String formatRating(BigDecimal rating) {
        if (rating == null) {
            return "N/A";
        }
        return RATING_FORMAT.format(rating);
    }

    /**
     * Normalizes a BigDecimal to standard scale for prices (2 decimal places)
     */
    public static BigDecimal normalizePriceScale(BigDecimal price) {
        if (price == null) {
            return null;
        }
        return price.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Normalizes a BigDecimal to standard scale for weights (2 decimal places)
     */
    public static BigDecimal normalizeWeightScale(BigDecimal weight) {
        if (weight == null) {
            return null;
        }
        return weight.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Normalizes a BigDecimal to standard scale for ratings (1 decimal place)
     */
    public static BigDecimal normalizeRatingScale(BigDecimal rating) {
        if (rating == null) {
            return null;
        }
        return rating.setScale(1, RoundingMode.HALF_UP);
    }

    // Format validation methods

    /**
     * Validates email format
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validates phone number format
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && PHONE_PATTERN.matcher(phoneNumber.replaceAll("\\s", "")).matches();
    }

    /**
     * Validates postal code format
     */
    public static boolean isValidPostalCode(String postalCode) {
        return postalCode != null && POSTAL_CODE_PATTERN.matcher(postalCode).matches();
    }

    /**
     * Validates that a string contains only printable ASCII characters
     */
    public static boolean isPrintableAscii(String input) {
        if (input == null) {
            return true;
        }
        return input.chars().allMatch(c -> c >= 32 && c <= 126);
    }

    /**
     * Validates that a string is safe for HTML display (no script tags, etc.)
     */
    public static boolean isSafeForHtml(String input) {
        if (input == null) {
            return true;
        }

        String lowerInput = input.toLowerCase();
        return !lowerInput.contains("<script") &&
                !lowerInput.contains("javascript:") &&
                !lowerInput.contains("onload=") &&
                !lowerInput.contains("onerror=");
    }

    // Data sanitization methods

    /**
     * Sanitizes a string for safe HTML display by escaping special characters
     */
    public static String sanitizeForHtml(String input) {
        if (input == null) {
            return null;
        }

        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    /**
     * Removes potentially dangerous characters from input
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }

        // Remove control characters except tab, newline, and carriage return
        return input.replaceAll("[\\p{Cntrl}&&[^\t\n\r]]", "");
    }

    /**
     * Truncates a string to a maximum length with ellipsis
     */
    public static String truncateWithEllipsis(String input, int maxLength) {
        if (input == null || input.length() <= maxLength) {
            return input;
        }

        if (maxLength <= 3) {
            return input.substring(0, maxLength);
        }

        return input.substring(0, maxLength - 3) + "...";
    }

    // Parsing and conversion utilities

    /**
     * Safely parses a string to BigDecimal
     */
    public static BigDecimal parseBigDecimal(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }

        try {
            return new BigDecimal(input.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format: " + input, e);
        }
    }

    /**
     * Safely parses a string to Long
     */
    public static Long parseLong(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }

        try {
            return Long.parseLong(input.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid long format: " + input, e);
        }
    }

    /**
     * Safely parses a string to Integer
     */
    public static Integer parseInteger(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid integer format: " + input, e);
        }
    }

    /**
     * Converts a boolean to a standardized string representation
     */
    public static String booleanToString(Boolean value) {
        if (value == null) {
            return null;
        }
        return value ? "true" : "false";
    }

    /**
     * Parses a string to boolean with flexible input handling
     */
    public static Boolean parseBoolean(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }

        String normalized = input.trim().toLowerCase();
        switch (normalized) {
            case "true":
            case "yes":
            case "1":
            case "on":
                return true;
            case "false":
            case "no":
            case "0":
            case "off":
                return false;
            default:
                throw new IllegalArgumentException("Invalid boolean format: " + input);
        }
    }

    // Validation helper methods

    /**
     * Validates and normalizes a product name
     */
    public static String validateAndNormalizeProductName(String productName) {
        ValidationUtil.validateProductName(productName);
        return normalizeName(productName);
    }

    /**
     * Validates and normalizes a brand name
     */
    public static String validateAndNormalizeBrandName(String brandName) {
        ValidationUtil.validateBrandName(brandName);
        return normalizeName(brandName);
    }

    /**
     * Validates and normalizes a category name
     */
    public static String validateAndNormalizeCategoryName(String categoryName) {
        ValidationUtil.validateCategoryName(categoryName);
        return normalizeName(categoryName);
    }

    /**
     * Validates and generates a category slug
     */
    public static String validateAndGenerateCategorySlug(String categoryName) {
        ValidationUtil.requireNonBlank(categoryName, "Category name");
        String slug = generateSlug(categoryName);
        ValidationUtil.validateCategorySlug(slug);
        return slug;
    }
}