package com.example.ecommerce.catalog.domain.specification;

import com.example.ecommerce.catalog.domain.Category;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Specification class for building dynamic queries for Category entity
 */
public class CategorySpecification {

    /**
     * Filter by category name (case-insensitive partial match)
     */
    public static Specification<Category> hasNameContaining(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + name.toLowerCase() + "%");
        };
    }

    /**
     * Filter by exact category name
     */
    public static Specification<Category> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("name"), name);
        };
    }

    /**
     * Filter by category slug
     */
    public static Specification<Category> hasSlug(String slug) {
        return (root, query, criteriaBuilder) -> {
            if (slug == null || slug.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("slug"), slug);
        };
    }

    /**
     * Filter by description (case-insensitive partial match)
     */
    public static Specification<Category> hasDescriptionContaining(String description) {
        return (root, query, criteriaBuilder) -> {
            if (description == null || description.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description")),
                    "%" + description.toLowerCase() + "%");
        };
    }

    /**
     * Filter by parent category
     */
    public static Specification<Category> hasParent(Category parent) {
        return (root, query, criteriaBuilder) -> {
            if (parent == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("parent"), parent);
        };
    }

    /**
     * Filter by parent category ID
     */
    public static Specification<Category> hasParentId(UUID parentId) {
        return (root, query, criteriaBuilder) -> {
            if (parentId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("parent").get("id"), parentId);
        };
    }

    /**
     * Filter root categories (no parent)
     */
    public static Specification<Category> isRootCategory() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get("parent"));
    }

    /**
     * Filter categories that have subcategories
     */
    public static Specification<Category> hasSubcategories() {
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .greaterThan(criteriaBuilder.size(root.get("subCategories")), 0);
    }

    /**
     * Filter categories that have no subcategories (leaf categories)
     */
    public static Specification<Category> isLeafCategory() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(criteriaBuilder.size(root.get("subCategories")),
                0);
    }

    /**
     * Filter categories that have products
     */
    public static Specification<Category> hasProducts() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(criteriaBuilder.size(root.get("products")),
                0);
    }

    /**
     * Filter categories that have no products
     */
    public static Specification<Category> hasNoProducts() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(criteriaBuilder.size(root.get("products")), 0);
    }

    /**
     * Filter empty leaf categories (no products and no subcategories)
     */
    public static Specification<Category> isEmptyLeafCategory() {
        return Specification.where(isLeafCategory()).and(hasNoProducts());
    }

    /**
     * Filter categories with minimum number of products
     */
    public static Specification<Category> hasMinimumProducts(int minCount) {
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .greaterThanOrEqualTo(criteriaBuilder.size(root.get("products")), minCount);
    }

    /**
     * Filter categories with maximum number of products
     */
    public static Specification<Category> hasMaximumProducts(int maxCount) {
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .lessThanOrEqualTo(criteriaBuilder.size(root.get("products")), maxCount);
    }

    /**
     * Filter categories with product count in range
     */
    public static Specification<Category> hasProductCountBetween(int minCount, int maxCount) {
        return Specification.where(hasMinimumProducts(minCount)).and(hasMaximumProducts(maxCount));
    }

    /**
     * Filter categories with minimum number of subcategories
     */
    public static Specification<Category> hasMinimumSubcategories(int minCount) {
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .greaterThanOrEqualTo(criteriaBuilder.size(root.get("subCategories")), minCount);
    }

    /**
     * Filter categories with maximum number of subcategories
     */
    public static Specification<Category> hasMaximumSubcategories(int maxCount) {
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .lessThanOrEqualTo(criteriaBuilder.size(root.get("subCategories")), maxCount);
    }

    /**
     * Search in name or description (full-text search)
     */
    public static Specification<Category> searchInNameOrDescription(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String likePattern = "%" + searchTerm.toLowerCase() + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern));
        };
    }

    /**
     * Filter categories by multiple parent IDs
     */
    public static Specification<Category> hasParentIdIn(List<UUID> parentIds) {
        return (root, query, criteriaBuilder) -> {
            if (parentIds == null || parentIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("parent").get("id").in(parentIds);
        };
    }

    /**
     * Filter categories by multiple category IDs
     */
    public static Specification<Category> hasIdIn(List<UUID> categoryIds) {
        return (root, query, criteriaBuilder) -> {
            if (categoryIds == null || categoryIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("id").in(categoryIds);
        };
    }

    /**
     * Filter categories excluding specific IDs
     */
    public static Specification<Category> hasIdNotIn(List<UUID> excludeIds) {
        return (root, query, criteriaBuilder) -> {
            if (excludeIds == null || excludeIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.not(root.get("id").in(excludeIds));
        };
    }

    /**
     * Complex filter for category hierarchy queries
     */
    public static Specification<Category> withHierarchyFilters(
            String name,
            String description,
            UUID parentId,
            Boolean isRoot,
            Boolean hasSubcategories,
            Boolean hasProducts,
            Integer minProductCount,
            Integer maxProductCount) {

        return Specification.where(hasNameContaining(name))
                .and(hasDescriptionContaining(description))
                .and(hasParentId(parentId))
                .and(isRoot != null && isRoot ? isRootCategory() : null)
                .and(hasSubcategories != null && hasSubcategories ? hasSubcategories() : null)
                .and(hasProducts != null && hasProducts ? hasProducts() : null)
                .and(minProductCount != null ? hasMinimumProducts(minProductCount) : null)
                .and(maxProductCount != null ? hasMaximumProducts(maxProductCount) : null);
    }

    /**
     * Filter for finding potential parent categories (excluding self and
     * descendants)
     */
    public static Specification<Category> isPotentialParentFor(UUID categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null) {
                return criteriaBuilder.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            // Exclude self
            predicates.add(criteriaBuilder.notEqual(root.get("id"), categoryId));

            // This is a simplified version - in a real implementation, you might need
            // a more complex query to exclude all descendants
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Filter categories that can be safely deleted (no products and no
     * subcategories)
     */
    public static Specification<Category> canBeDeleted() {
        return Specification.where(hasNoProducts()).and(isLeafCategory());
    }

    /**
     * Filter active categories (categories with products or subcategories)
     */
    public static Specification<Category> isActive() {
        return Specification.where(hasProducts()).or(hasSubcategories());
    }

    /**
     * Filter categories by creation date range
     */
    public static Specification<Category> createdBetween(java.time.LocalDateTime startDate,
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
     * Filter categories by last update date range
     */
    public static Specification<Category> updatedBetween(java.time.LocalDateTime startDate,
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
     * Filter categories by depth in hierarchy
     */
    public static Specification<Category> hasDepth(int depth) {
        return (root, query, criteriaBuilder) -> {
            if (depth == 0) {
                return isRootCategory().toPredicate(root, query, criteriaBuilder);
            }

            // This is a simplified version - calculating depth would need recursive queries
            // For now, approximate by checking parent relationships
            if (depth == 1) {
                return criteriaBuilder.and(
                        criteriaBuilder.isNotNull(root.get("parent")),
                        criteriaBuilder.isNull(root.get("parent").get("parent")));
            }

            return criteriaBuilder.conjunction();
        };
    }

    /**
     * Filter categories with products in specific status
     */
    public static Specification<Category> hasProductsWithStatus(
            com.example.ecommerce.catalog.domain.Product.Status productStatus) {
        return (root, query, criteriaBuilder) -> {
            if (productStatus == null) {
                return criteriaBuilder.conjunction();
            }

            var productJoin = root.join("products", jakarta.persistence.criteria.JoinType.INNER);
            return criteriaBuilder.equal(productJoin.get("status"), productStatus);
        };
    }

    /**
     * Filter categories with products in stock
     */
    public static Specification<Category> hasProductsInStock() {
        return (root, query, criteriaBuilder) -> {
            var productJoin = root.join("products", jakarta.persistence.criteria.JoinType.INNER);
            return criteriaBuilder.greaterThan(productJoin.get("stockQuantity"), 0L);
        };
    }

    /**
     * Filter categories with products in price range
     */
    public static Specification<Category> hasProductsInPriceRange(java.math.BigDecimal minPrice,
            java.math.BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            var productJoin = root.join("products", jakarta.persistence.criteria.JoinType.INNER);

            List<Predicate> predicates = new ArrayList<>();

            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(productJoin.get("price"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(productJoin.get("price"), maxPrice));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Filter categories by total product count across hierarchy
     */
    public static Specification<Category> hasTotalProductCountGreaterThan(int minCount) {
        return (root, query, criteriaBuilder) -> {
            // This would need a complex recursive query in a real implementation
            // For now, just check direct products
            return criteriaBuilder.greaterThan(criteriaBuilder.size(root.get("products")), minCount);
        };
    }

    /**
     * Filter categories with specific slug pattern
     */
    public static Specification<Category> hasSlugMatching(String slugPattern) {
        return (root, query, criteriaBuilder) -> {
            if (slugPattern == null || slugPattern.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get("slug"), slugPattern.replace("*", "%"));
        };
    }

    /**
     * Filter categories that are ancestors of a specific category
     */
    public static Specification<Category> isAncestorOf(UUID categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null) {
                return criteriaBuilder.conjunction();
            }

            // This would need a recursive CTE in a real implementation
            // For now, return a basic condition
            return criteriaBuilder.conjunction();
        };
    }

    /**
     * Filter categories that are descendants of a specific category
     */
    public static Specification<Category> isDescendantOf(UUID categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null) {
                return criteriaBuilder.conjunction();
            }

            // This would need a recursive CTE in a real implementation
            // For now, just check direct parent
            return criteriaBuilder.equal(root.get("parent").get("id"), categoryId);
        };
    }

    /**
     * Filter categories with recently added products
     */
    public static Specification<Category> hasRecentProducts(java.time.LocalDateTime since) {
        return (root, query, criteriaBuilder) -> {
            if (since == null) {
                return criteriaBuilder.conjunction();
            }

            var productJoin = root.join("products", jakarta.persistence.criteria.JoinType.INNER);
            return criteriaBuilder.greaterThan(productJoin.get("createdAt"), since);
        };
    }

    /**
     * Filter categories needing attention (empty leaf categories or categories with
     * only out-of-stock products)
     */
    public static Specification<Category> needsAttention() {
        return Specification.where(isEmptyLeafCategory())
                .or(Specification.where(hasProducts()).and(
                        (root, query, criteriaBuilder) -> {
                            var productJoin = root.join("products", jakarta.persistence.criteria.JoinType.LEFT);
                            return criteriaBuilder.equal(productJoin.get("stockQuantity"), 0L);
                        }));
    }

    /**
     * Advanced complex filter for category hierarchy queries with additional
     * criteria
     */
    public static Specification<Category> withAdvancedFilters(
            String searchTerm,
            UUID parentId,
            Boolean isRoot,
            Boolean hasSubcategories,
            Boolean hasProducts,
            Integer minProductCount,
            Integer maxProductCount,
            Integer depth,
            java.time.LocalDateTime createdAfter,
            java.time.LocalDateTime updatedAfter,
            com.example.ecommerce.catalog.domain.Product.Status productStatus) {

        return Specification.where(searchInNameOrDescription(searchTerm))
                .and(hasParentId(parentId))
                .and(isRoot != null && isRoot ? isRootCategory() : null)
                .and(hasSubcategories != null && hasSubcategories ? hasSubcategories() : null)
                .and(hasProducts != null && hasProducts ? hasProducts() : null)
                .and(minProductCount != null ? hasMinimumProducts(minProductCount) : null)
                .and(maxProductCount != null ? hasMaximumProducts(maxProductCount) : null)
                .and(depth != null ? hasDepth(depth) : null)
                .and(createdBetween(createdAfter, null))
                .and(updatedBetween(updatedAfter, null))
                .and(hasProductsWithStatus(productStatus));
    }
}