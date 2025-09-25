package com.example.ecommerce.catalog.util;

import com.example.ecommerce.catalog.domain.Category;
import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.domain.ProductImage;
import com.example.ecommerce.catalog.domain.ProductVariant;

import java.util.List;
import java.util.Map;

/**
 * Utility class for validating business constraints and relationships
 * across domain entities in the catalog system.
 */
public final class ConstraintValidationUtil {

    private ConstraintValidationUtil() {
        // Utility class - prevent instantiation
    }

    // Product constraint validations

    /**
     * Validates that a product can be created with the given category
     */
    public static void validateProductCategoryConstraint(Category category) {
        ValidationUtil.requireNonNull(category, "Category");

        if (category.getId() == null) {
            throw new IllegalArgumentException("Category must be persisted before assigning to product");
        }
    }

    /**
     * Validates that a product variant name is unique within the product
     */
    public static void validateUniqueVariantName(Product product, String variantName, ProductVariant excludeVariant) {
        ValidationUtil.requireNonNull(product, "Product");
        ValidationUtil.requireNonBlank(variantName, "Variant name");

        boolean duplicateExists = product.getProductVariants().stream()
                .filter(variant -> excludeVariant == null || !variant.equals(excludeVariant))
                .anyMatch(variant -> variant.getVariantName().equalsIgnoreCase(variantName.trim()));

        if (duplicateExists) {
            throw new IllegalArgumentException(
                    "Variant with name '" + variantName + "' already exists for this product");
        }
    }

    /**
     * Validates that only one primary image exists per product
     */
    public static void validateSinglePrimaryImageConstraint(Product product, ProductImage newPrimaryImage) {
        ValidationUtil.requireNonNull(product, "Product");
        ValidationUtil.requireNonNull(newPrimaryImage, "Primary image");

        if (!product.getProductImages().contains(newPrimaryImage)) {
            throw new IllegalArgumentException("Image must belong to this product to be set as primary");
        }

        long primaryImageCount = product.getProductImages().stream()
                .filter(ProductImage::isPrimary)
                .filter(image -> !image.equals(newPrimaryImage))
                .count();

        if (primaryImageCount > 0) {
            throw new IllegalArgumentException("Product can have only one primary image");
        }
    }

    /**
     * Validates that a product image belongs to the specified product
     */
    public static void validateImageOwnership(Product product, ProductImage image) {
        ValidationUtil.requireNonNull(product, "Product");
        ValidationUtil.requireNonNull(image, "Image");

        if (!product.getProductImages().contains(image)) {
            throw new IllegalArgumentException("Image does not belong to this product");
        }
    }

    /**
     * Validates that a product variant belongs to the specified product
     */
    public static void validateVariantOwnership(Product product, ProductVariant variant) {
        ValidationUtil.requireNonNull(product, "Product");
        ValidationUtil.requireNonNull(variant, "Variant");

        if (!product.getProductVariants().contains(variant)) {
            throw new IllegalArgumentException("Variant does not belong to this product");
        }
    }

    // Category constraint validations

    /**
     * Validates that a category hierarchy doesn't create circular references
     */
    public static void validateNoCyclicCategoryReference(Category category, Category newParent) {
        ValidationUtil.requireNonNull(category, "Category");

        if (newParent == null) {
            return; // Root category is valid
        }

        if (newParent.equals(category)) {
            throw new IllegalArgumentException("Category cannot be its own parent");
        }

        if (category.isAncestorOf(newParent)) {
            throw new IllegalArgumentException("Cannot set parent category that would create a circular reference");
        }
    }

    /**
     * Validates that a category can be safely deleted
     */
    public static void validateCategoryDeletionConstraints(Category category) {
        ValidationUtil.requireNonNull(category, "Category");

        if (category.hasProducts()) {
            throw new IllegalArgumentException(
                    "Cannot delete category '" + category.getName() + "' because it contains " +
                            category.getProducts().size() + " products");
        }

        if (category.hasSubCategories()) {
            throw new IllegalArgumentException(
                    "Cannot delete category '" + category.getName() + "' because it has " +
                            category.getSubCategories().size() + " subcategories");
        }
    }

    /**
     * Validates that a category slug is unique (excluding the category being
     * updated)
     */
    public static void validateUniqueCategorySlug(String slug, Category excludeCategory,
            List<Category> existingCategories) {
        ValidationUtil.requireNonBlank(slug, "Category slug");
        ValidationUtil.requireNonNull(existingCategories, "Existing categories");

        boolean duplicateExists = existingCategories.stream()
                .filter(category -> excludeCategory == null || !category.equals(excludeCategory))
                .anyMatch(category -> category.getSlug().equalsIgnoreCase(slug.trim()));

        if (duplicateExists) {
            throw new IllegalArgumentException("Category slug '" + slug + "' already exists");
        }
    }

    /**
     * Validates that a category name is unique (excluding the category being
     * updated)
     */
    public static void validateUniqueCategoryName(String name, Category excludeCategory,
            List<Category> existingCategories) {
        ValidationUtil.requireNonBlank(name, "Category name");
        ValidationUtil.requireNonNull(existingCategories, "Existing categories");

        boolean duplicateExists = existingCategories.stream()
                .filter(category -> excludeCategory == null || !category.equals(excludeCategory))
                .anyMatch(category -> category.getName().equalsIgnoreCase(name.trim()));

        if (duplicateExists) {
            throw new IllegalArgumentException("Category name '" + name + "' already exists");
        }
    }

    // Product variant constraint validations

    /**
     * Validates that variant attributes have valid names and values
     */
    public static void validateVariantAttributes(Map<String, String> attributes) {
        if (attributes == null) {
            return;
        }

        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            ValidationUtil.requireNonBlank(entry.getKey(), "Attribute name");
            ValidationUtil.requireMaxLength(entry.getKey(), 50, "Attribute name");

            if (entry.getValue() != null) {
                ValidationUtil.requireMaxLength(entry.getValue(), 255, "Attribute value");
            }
        }
    }

    /**
     * Validates that a variant SKU is unique across all variants (excluding the
     * variant being updated)
     */
    public static void validateUniqueVariantSku(String sku, ProductVariant excludeVariant,
            List<ProductVariant> existingVariants) {
        if (sku == null || sku.trim().isEmpty()) {
            return; // SKU is optional
        }

        ValidationUtil.requireNonNull(existingVariants, "Existing variants");

        boolean duplicateExists = existingVariants.stream()
                .filter(variant -> excludeVariant == null || !variant.equals(excludeVariant))
                .anyMatch(variant -> sku.equalsIgnoreCase(variant.getSku()));

        if (duplicateExists) {
            throw new IllegalArgumentException("Variant SKU '" + sku + "' already exists");
        }
    }

    // Product image constraint validations

    /**
     * Validates that image display orders are unique within a product
     */
    public static void validateUniqueImageDisplayOrder(Product product, Integer displayOrder,
            ProductImage excludeImage) {
        ValidationUtil.requireNonNull(product, "Product");

        if (displayOrder == null) {
            return; // Display order is optional
        }

        boolean duplicateExists = product.getProductImages().stream()
                .filter(image -> excludeImage == null || !image.equals(excludeImage))
                .anyMatch(image -> displayOrder.equals(image.getDisplayOrder()));

        if (duplicateExists) {
            throw new IllegalArgumentException(
                    "Display order " + displayOrder + " is already used by another image in this product");
        }
    }

    /**
     * Validates that a product has at least one image after removal
     */
    public static void validateMinimumImageRequirement(Product product, ProductImage imageToRemove,
            boolean requireMinimumImages) {
        ValidationUtil.requireNonNull(product, "Product");
        ValidationUtil.requireNonNull(imageToRemove, "Image to remove");

        if (!requireMinimumImages) {
            return;
        }

        long remainingImages = product.getProductImages().stream()
                .filter(image -> !image.equals(imageToRemove))
                .count();

        if (remainingImages == 0) {
            throw new IllegalArgumentException("Cannot remove the last image from a product");
        }
    }

    /**
     * Validates that removing a primary image won't leave the product without a
     * primary image
     */
    public static void validatePrimaryImageRemoval(Product product, ProductImage imageToRemove) {
        ValidationUtil.requireNonNull(product, "Product");
        ValidationUtil.requireNonNull(imageToRemove, "Image to remove");

        if (!imageToRemove.isPrimary()) {
            return; // Not removing primary image
        }

        long otherImages = product.getProductImages().stream()
                .filter(image -> !image.equals(imageToRemove))
                .count();

        if (otherImages > 0) {
            // Suggest promoting another image to primary
            throw new IllegalArgumentException(
                    "Cannot remove primary image. Please set another image as primary first, or ensure this is the last image being removed");
        }
    }

    // Stock management constraint validations

    /**
     * Validates that stock operations maintain non-negative stock levels
     */
    public static void validateStockOperation(long currentStock, long stockChange, String operation) {
        long resultingStock = currentStock + stockChange;

        if (resultingStock < 0) {
            throw new IllegalArgumentException(
                    "Cannot " + operation + " " + Math.abs(stockChange) + " units. Only " + currentStock
                            + " units available");
        }
    }

    /**
     * Validates that bulk stock operations are consistent
     */
    public static void validateBulkStockOperation(Map<String, Long> stockChanges) {
        ValidationUtil.requireNonNull(stockChanges, "Stock changes");

        for (Map.Entry<String, Long> entry : stockChanges.entrySet()) {
            ValidationUtil.requireNonBlank(entry.getKey(), "Product/Variant identifier");
            ValidationUtil.requireNonNull(entry.getValue(), "Stock change amount");
        }
    }

    // Business rule constraint validations

    /**
     * Validates that a product status change is allowed based on current state
     */
    public static void validateProductStatusTransition(Product.Status currentStatus, Product.Status newStatus,
            long stockQuantity) {
        ValidationUtil.requireNonNull(currentStatus, "Current status");
        ValidationUtil.requireNonNull(newStatus, "New status");

        // Cannot set to ACTIVE if no stock
        if (newStatus == Product.Status.ACTIVE && stockQuantity == 0) {
            throw new IllegalArgumentException("Cannot set product status to ACTIVE when stock quantity is 0");
        }

        // Cannot set to OUT_OF_STOCK if stock is available
        if (newStatus == Product.Status.OUT_OF_STOCK && stockQuantity > 0) {
            throw new IllegalArgumentException("Cannot set product status to OUT_OF_STOCK when stock is available");
        }
    }

    /**
     * Validates that a variant status change is allowed based on current state
     */
    public static void validateVariantStatusTransition(ProductVariant.Status currentStatus,
            ProductVariant.Status newStatus, long stockQuantity) {
        ValidationUtil.requireNonNull(currentStatus, "Current status");
        ValidationUtil.requireNonNull(newStatus, "New status");

        // Cannot set to ACTIVE if no stock
        if (newStatus == ProductVariant.Status.ACTIVE && stockQuantity == 0) {
            throw new IllegalArgumentException("Cannot set variant status to ACTIVE when stock quantity is 0");
        }

        // Cannot set to OUT_OF_STOCK if stock is available
        if (newStatus == ProductVariant.Status.OUT_OF_STOCK && stockQuantity > 0) {
            throw new IllegalArgumentException("Cannot set variant status to OUT_OF_STOCK when stock is available");
        }
    }
}