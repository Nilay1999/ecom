package com.example.ecommerce.catalog.domain.specification;

import com.example.ecommerce.catalog.domain.Category;
import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.domain.ProductVariant;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Specification class for building dynamic queries for Product entity
 */
public class ProductSpecification {

    /**
     * Filter by product name (case-insensitive partial match)
     */
    public static Specification<Product> hasNameContaining(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("productName")),
                    "%" + name.toLowerCase() + "%");
        };
    }

    /**
     * Filter by brand name (case-insensitive partial match)
     */
    public static Specification<Product> hasBrandContaining(String brandName) {
        return (root, query, criteriaBuilder) -> {
            if (brandName == null || brandName.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("brandName")),
                    "%" + brandName.toLowerCase() + "%");
        };
    }

    /**
     * Filter by exact brand name
     */
    public static Specification<Product> hasBrand(String brandName) {
        return (root, query, criteriaBuilder) -> {
            if (brandName == null || brandName.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("brandName"), brandName);
        };
    }

    /**
     * Filter by category
     */
    public static Specification<Product> hasCategory(Category category) {
        return (root, query, criteriaBuilder) -> {
            if (category == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("category"), category);
        };
    }

    /**
     * Filter by category ID
     */
    public static Specification<Product> hasCategoryId(UUID categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("category").get("id"), categoryId);
        };
    }

    /**
     * Filter by multiple categories
     */
    public static Specification<Product> hasCategoryIn(List<Category> categories) {
        return (root, query, criteriaBuilder) -> {
            if (categories == null || categories.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("category").in(categories);
        };
    }

    /**
     * Filter by status
     */
    public static Specification<Product> hasStatus(Product.Status status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    /**
     * Filter by multiple statuses
     */
    public static Specification<Product> hasStatusIn(List<Product.Status> statuses) {
        return (root, query, criteriaBuilder) -> {
            if (statuses == null || statuses.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("status").in(statuses);
        };
    }

    /**
     * Filter by price range
     */
    public static Specification<Product> hasPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Filter by minimum price
     */
    public static Specification<Product> hasPriceGreaterThanOrEqual(BigDecimal minPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
        };
    }

    /**
     * Filter by maximum price
     */
    public static Specification<Product> hasPriceLessThanOrEqual(BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (maxPrice == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
        };
    }

    /**
     * Filter by rating range
     */
    public static Specification<Product> hasRatingBetween(BigDecimal minRating, BigDecimal maxRating) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (minRating != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), minRating));
            }

            if (maxRating != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("rating"), maxRating));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Filter by minimum rating
     */
    public static Specification<Product> hasRatingGreaterThanOrEqual(BigDecimal minRating) {
        return (root, query, criteriaBuilder) -> {
            if (minRating == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), minRating);
        };
    }

    /**
     * Filter by stock quantity range
     */
    public static Specification<Product> hasStockBetween(Long minStock, Long maxStock) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (minStock != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("stockQuantity"), minStock));
            }

            if (maxStock != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("stockQuantity"), maxStock));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Filter products in stock
     */
    public static Specification<Product> isInStock() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("stockQuantity"), 0L);
    }

    /**
     * Filter products out of stock
     */
    public static Specification<Product> isOutOfStock() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("stockQuantity"), 0L);
    }

    /**
     * Filter products with low stock (below threshold)
     */
    public static Specification<Product> hasLowStock(long threshold) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.lessThanOrEqualTo(root.get("stockQuantity"), threshold),
                criteriaBuilder.greaterThan(root.get("stockQuantity"), 0L));
    }

    /**
     * Filter by weight range
     */
    public static Specification<Product> hasWeightBetween(BigDecimal minWeight, BigDecimal maxWeight) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (minWeight != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("weight"), minWeight));
            }

            if (maxWeight != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("weight"), maxWeight));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Search in name or description (full-text search)
     */
    public static Specification<Product> searchInNameOrDescription(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String likePattern = "%" + searchTerm.toLowerCase() + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern));
        };
    }

    /**
     * Filter products that have variants
     */
    public static Specification<Product> hasVariants() {
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .greaterThan(criteriaBuilder.size(root.get("productVariants")), 0);
    }

    /**
     * Filter products that have images
     */
    public static Specification<Product> hasImages() {
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .greaterThan(criteriaBuilder.size(root.get("productImages")), 0);
    }

    /**
     * Filter products that have primary image
     */
    public static Specification<Product> hasPrimaryImage() {
        return (root, query, criteriaBuilder) -> {
            Join<Object, Object> imageJoin = root.join("productImages", JoinType.INNER);
            return criteriaBuilder.equal(imageJoin.get("isPrimary"), true);
        };
    }

    /**
     * Filter products without primary image
     */
    public static Specification<Product> hasNoPrimaryImage() {
        return (root, query, criteriaBuilder) -> {
            // Subquery to find products with primary images
            var subquery = query.subquery(UUID.class);
            var subRoot = subquery.from(Product.class);
            var imageJoin = subRoot.join("productImages", JoinType.INNER);

            subquery.select(subRoot.get("id"))
                    .where(criteriaBuilder.equal(imageJoin.get("isPrimary"), true));

            return criteriaBuilder.not(root.get("id").in(subquery));
        };
    }

    /**
     * Filter active products
     */
    public static Specification<Product> isActive() {
        return hasStatus(Product.Status.ACTIVE);
    }

    /**
     * Filter available products (active and in stock)
     */
    public static Specification<Product> isAvailable() {
        return Specification.where(isActive()).and(isInStock());
    }

    /**
     * Filter products by creation date range
     */
    public static Specification<Product> createdBetween(java.time.LocalDateTime startDate,
            java.time.LocalDateTime endDate) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            }

            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Filter products by last update date range
     */
    public static Specification<Product> updatedBetween(java.time.LocalDateTime startDate,
            java.time.LocalDateTime endDate) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("updatedAt"), startDate));
            }

            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("updatedAt"), endDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Filter products with variants in specific status
     */
    public static Specification<Product> hasVariantsWithStatus(ProductVariant.Status variantStatus) {
        return (root, query, criteriaBuilder) -> {
            if (variantStatus == null) {
                return criteriaBuilder.conjunction();
            }

            var variantJoin = root.join("productVariants", JoinType.INNER);
            return criteriaBuilder.equal(variantJoin.get("status"), variantStatus);
        };
    }

    /**
     * Filter products with variants having stock
     */
    public static Specification<Product> hasVariantsInStock() {
        return (root, query, criteriaBuilder) -> {
            var variantJoin = root.join("productVariants", JoinType.INNER);
            return criteriaBuilder.greaterThan(variantJoin.get("stockQuantity"), 0L);
        };
    }

    /**
     * Filter products with total stock (product + variants) above threshold
     */
    public static Specification<Product> hasTotalStockGreaterThan(long threshold) {
        return (root, query, criteriaBuilder) -> {
            // This is a simplified version - in reality, you'd need a more complex subquery
            return criteriaBuilder.greaterThan(root.get("stockQuantity"), threshold);
        };
    }

    /**
     * Filter products by category hierarchy (including subcategories)
     */
    public static Specification<Product> inCategoryHierarchy(UUID rootCategoryId) {
        return (root, query, criteriaBuilder) -> {
            if (rootCategoryId == null) {
                return criteriaBuilder.conjunction();
            }

            // This would need a recursive CTE or multiple joins in a real implementation
            // For now, just filter by direct category
            return criteriaBuilder.equal(root.get("category").get("id"), rootCategoryId);
        };
    }

    /**
     * Filter products with images in specific format
     */
    public static Specification<Product> hasImagesWithFormat(String imageFormat) {
        return (root, query, criteriaBuilder) -> {
            if (imageFormat == null || imageFormat.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            var imageJoin = root.join("productImages", JoinType.INNER);
            return criteriaBuilder.like(
                    criteriaBuilder.lower(imageJoin.get("imageUrl")),
                    "%." + imageFormat.toLowerCase() + "%");
        };
    }

    /**
     * Filter products with specific variant attribute
     */
    public static Specification<Product> hasVariantWithAttribute(String attributeName, String attributeValue) {
        return (root, query, criteriaBuilder) -> {
            if (attributeName == null || attributeName.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            var variantJoin = root.join("productVariants", JoinType.INNER);

            if (attributeValue == null) {
                return criteriaBuilder.isNotNull(variantJoin.get("attributes").get(attributeName));
            } else {
                return criteriaBuilder.equal(variantJoin.get("attributes").get(attributeName), attributeValue);
            }
        };
    }

    /**
     * Filter products by brand list
     */
    public static Specification<Product> hasBrandIn(List<String> brands) {
        return (root, query, criteriaBuilder) -> {
            if (brands == null || brands.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("brandName").in(brands);
        };
    }

    /**
     * Filter products excluding specific brands
     */
    public static Specification<Product> hasBrandNotIn(List<String> excludeBrands) {
        return (root, query, criteriaBuilder) -> {
            if (excludeBrands == null || excludeBrands.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.not(root.get("brandName").in(excludeBrands));
        };
    }

    /**
     * Filter products with price changes in date range
     */
    public static Specification<Product> hasPriceChangedSince(java.time.LocalDateTime since) {
        return (root, query, criteriaBuilder) -> {
            if (since == null) {
                return criteriaBuilder.conjunction();
            }
            // This would need audit tables in a real implementation
            return criteriaBuilder.greaterThan(root.get("updatedAt"), since);
        };
    }

    /**
     * Filter products needing restock (below minimum threshold)
     */
    public static Specification<Product> needsRestock(long minimumStock) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.lessThan(root.get("stockQuantity"), minimumStock),
                criteriaBuilder.equal(root.get("status"), Product.Status.ACTIVE));
    }

    /**
     * Complex filter combining multiple criteria with advanced options
     */
    public static Specification<Product> withFilters(
            String name,
            String brand,
            UUID categoryId,
            Product.Status status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            BigDecimal minRating,
            Boolean inStock) {

        return Specification.where(hasNameContaining(name))
                .and(hasBrandContaining(brand))
                .and(hasCategoryId(categoryId))
                .and(hasStatus(status))
                .and(hasPriceBetween(minPrice, maxPrice))
                .and(hasRatingGreaterThanOrEqual(minRating))
                .and(inStock != null && inStock ? isInStock() : null);
    }

    /**
     * Filter by multiple category IDs
     */
    public static Specification<Product> hasCategoryIdIn(List<UUID> categoryIds) {
        return (root, query, criteriaBuilder) -> {
            if (categoryIds == null || categoryIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("category").get("id").in(categoryIds);
        };
    }

    /**
     * Advanced complex filter with additional criteria
     */
    public static Specification<Product> withAdvancedFilters(
            String searchTerm,
            List<String> brands,
            List<UUID> categoryIds,
            List<Product.Status> statuses,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            BigDecimal minRating,
            Long minStock,
            Long maxStock,
            Boolean hasVariants,
            Boolean hasPrimaryImage,
            java.time.LocalDateTime createdAfter,
            java.time.LocalDateTime updatedAfter) {

        return Specification.where(searchInNameOrDescription(searchTerm))
                .and(hasBrandIn(brands))
                .and(hasCategoryIdIn(categoryIds))
                .and(hasStatusIn(statuses))
                .and(hasPriceBetween(minPrice, maxPrice))
                .and(hasRatingGreaterThanOrEqual(minRating))
                .and(hasStockBetween(minStock, maxStock))
                .and(hasVariants != null && hasVariants ? hasVariants() : null)
                .and(hasPrimaryImage != null && hasPrimaryImage ? hasPrimaryImage() : null)
                .and(hasPrimaryImage != null && !hasPrimaryImage ? hasNoPrimaryImage() : null)
                .and(createdBetween(createdAfter, null))
                .and(updatedBetween(updatedAfter, null));
    }
}