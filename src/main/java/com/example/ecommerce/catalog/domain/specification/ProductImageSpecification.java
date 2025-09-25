package com.example.ecommerce.catalog.domain.specification;

import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.domain.ProductImage;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Specification class for building dynamic queries for ProductImage entity
 */
public class ProductImageSpecification {

    /**
     * Filter by product
     */
    public static Specification<ProductImage> belongsToProduct(Product product) {
        return (root, query, criteriaBuilder) -> {
            if (product == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("product"), product);
        };
    }

    /**
     * Filter by product ID
     */
    public static Specification<ProductImage> belongsToProductId(UUID productId) {
        return (root, query, criteriaBuilder) -> {
            if (productId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("product").get("id"), productId);
        };
    }

    /**
     * Filter by multiple product IDs
     */
    public static Specification<ProductImage> belongsToProductIds(List<UUID> productIds) {
        return (root, query, criteriaBuilder) -> {
            if (productIds == null || productIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("product").get("id").in(productIds);
        };
    }

    /**
     * Filter primary images only
     */
    public static Specification<ProductImage> isPrimary() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isPrimary"), true);
    }

    /**
     * Filter secondary images only
     */
    public static Specification<ProductImage> isSecondary() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isPrimary"), false);
    }

    /**
     * Filter by image URL containing text
     */
    public static Specification<ProductImage> hasUrlContaining(String urlPart) {
        return (root, query, criteriaBuilder) -> {
            if (urlPart == null || urlPart.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("imageUrl")),
                    "%" + urlPart.toLowerCase() + "%");
        };
    }

    /**
     * Filter by alt text containing text
     */
    public static Specification<ProductImage> hasAltTextContaining(String altText) {
        return (root, query, criteriaBuilder) -> {
            if (altText == null || altText.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("altText")),
                    "%" + altText.toLowerCase() + "%");
        };
    }

    /**
     * Filter by display order
     */
    public static Specification<ProductImage> hasDisplayOrder(Integer displayOrder) {
        return (root, query, criteriaBuilder) -> {
            if (displayOrder == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("displayOrder"), displayOrder);
        };
    }

    /**
     * Filter by display order range
     */
    public static Specification<ProductImage> hasDisplayOrderBetween(Integer minOrder, Integer maxOrder) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (minOrder != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("displayOrder"), minOrder));
            }

            if (maxOrder != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("displayOrder"), maxOrder));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Filter images with alt text
     */
    public static Specification<ProductImage> hasAltText() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.isNotNull(root.get("altText")),
                criteriaBuilder.notEqual(root.get("altText"), ""));
    }

    /**
     * Filter images without alt text
     */
    public static Specification<ProductImage> hasNoAltText() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.isNull(root.get("altText")),
                criteriaBuilder.equal(root.get("altText"), ""));
    }

    /**
     * Filter by image format (based on URL extension)
     */
    public static Specification<ProductImage> hasImageFormat(String format) {
        return (root, query, criteriaBuilder) -> {
            if (format == null || format.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("imageUrl")),
                    "%." + format.toLowerCase() + "%");
        };
    }

    /**
     * Filter by multiple image formats
     */
    public static Specification<ProductImage> hasImageFormatIn(List<String> formats) {
        return (root, query, criteriaBuilder) -> {
            if (formats == null || formats.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();
            for (String format : formats) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("imageUrl")),
                        "%." + format.toLowerCase() + "%"));
            }

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Filter images by URL domain
     */
    public static Specification<ProductImage> hasUrlDomain(String domain) {
        return (root, query, criteriaBuilder) -> {
            if (domain == null || domain.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("imageUrl")),
                    "%://" + domain.toLowerCase() + "/%");
        };
    }

    /**
     * Filter images ordered by display order
     */
    public static Specification<ProductImage> orderedByDisplayOrder() {
        return (root, query, criteriaBuilder) -> {
            if (query != null) {
                query.orderBy(criteriaBuilder.asc(root.get("displayOrder")));
            }
            return criteriaBuilder.conjunction();
        };
    }

    /**
     * Complex filter for product images
     */
    public static Specification<ProductImage> withFilters(
            UUID productId,
            Boolean isPrimary,
            String urlContains,
            String altTextContains,
            Integer minDisplayOrder,
            Integer maxDisplayOrder,
            List<String> imageFormats) {

        return Specification.where(belongsToProductId(productId))
                .and(isPrimary != null && isPrimary ? isPrimary() : null)
                .and(isPrimary != null && !isPrimary ? isSecondary() : null)
                .and(hasUrlContaining(urlContains))
                .and(hasAltTextContaining(altTextContains))
                .and(hasDisplayOrderBetween(minDisplayOrder, maxDisplayOrder))
                .and(hasImageFormatIn(imageFormats));
    }

    /**
     * Filter for finding orphaned images (images without products)
     */
    public static Specification<ProductImage> isOrphaned() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get("product"));
    }

    /**
     * Filter for finding images that need alt text
     */
    public static Specification<ProductImage> needsAltText() {
        return Specification.where(hasNoAltText()).and(isPrimary());
    }

    /**
     * Filter for finding duplicate display orders within products
     */
    public static Specification<ProductImage> hasDuplicateDisplayOrder() {
        return (root, query, criteriaBuilder) -> {
            // This is a complex query that would typically be handled at the service level
            // For now, return a conjunction as this would need a subquery
            return criteriaBuilder.conjunction();
        };
    }
}