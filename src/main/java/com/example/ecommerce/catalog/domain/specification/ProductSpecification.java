package com.example.ecommerce.catalog.domain.specification;

import com.example.ecommerce.catalog.domain.Category;
import com.example.ecommerce.catalog.domain.Product;
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
     * Complex filter combining multiple criteria
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
}