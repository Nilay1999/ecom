package com.example.ecommerce.catalog.domain.specification;

import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.domain.ProductVariant;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Specification class for building dynamic queries for ProductVariant entity
 */
public class ProductVariantSpecification {

    /**
     * Filter by product
     */
    public static Specification<ProductVariant> belongsToProduct(Product product) {
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
    public static Specification<ProductVariant> belongsToProductId(UUID productId) {
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
    public static Specification<ProductVariant> belongsToProductIds(List<UUID> productIds) {
        return (root, query, criteriaBuilder) -> {
            if (productIds == null || productIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("product").get("id").in(productIds);
        };
    }

    /**
     * Filter by variant name (case-insensitive partial match)
     */
    public static Specification<ProductVariant> hasNameContaining(String variantName) {
        return (root, query, criteriaBuilder) -> {
            if (variantName == null || variantName.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("variantName")),
                    "%" + variantName.toLowerCase() + "%");
        };
    }

    /**
     * Filter by exact variant name
     */
    public static Specification<ProductVariant> hasName(String variantName) {
        return (root, query, criteriaBuilder) -> {
            if (variantName == null || variantName.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("variantName"), variantName);
        };
    }

    /**
     * Filter by SKU
     */
    public static Specification<ProductVariant> hasSku(String sku) {
        return (root, query, criteriaBuilder) -> {
            if (sku == null || sku.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("sku"), sku);
        };
    }

    /**
     * Filter by SKU containing text
     */
    public static Specification<ProductVariant> hasSkuContaining(String skuPart) {
        return (root, query, criteriaBuilder) -> {
            if (skuPart == null || skuPart.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("sku")),
                    "%" + skuPart.toLowerCase() + "%");
        };
    }

    /**
     * Filter by status
     */
    public static Specification<ProductVariant> hasStatus(ProductVariant.Status status) {
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
    public static Specification<ProductVariant> hasStatusIn(List<ProductVariant.Status> statuses) {
        return (root, query, criteriaBuilder) -> {
            if (statuses == null || statuses.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("status").in(statuses);
        };
    }

    /**
     * Filter by price override range
     */
    public static Specification<ProductVariant> hasPriceOverrideBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("priceOverride"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("priceOverride"), maxPrice));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Filter variants with price override
     */
    public static Specification<ProductVariant> hasPriceOverride() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("priceOverride"),
                BigDecimal.ZERO);
    }

    /**
     * Filter variants without price override
     */
    public static Specification<ProductVariant> hasNoPriceOverride() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("priceOverride"), BigDecimal.ZERO);
    }

    /**
     * Filter by stock quantity range
     */
    public static Specification<ProductVariant> hasStockBetween(Long minStock, Long maxStock) {
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
     * Filter variants in stock
     */
    public static Specification<ProductVariant> isInStock() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("stockQuantity"), 0L);
    }

    /**
     * Filter variants out of stock
     */
    public static Specification<ProductVariant> isOutOfStock() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("stockQuantity"), 0L);
    }

    /**
     * Filter variants with low stock (below threshold)
     */
    public static Specification<ProductVariant> hasLowStock(long threshold) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.lessThanOrEqualTo(root.get("stockQuantity"), threshold),
                criteriaBuilder.greaterThan(root.get("stockQuantity"), 0L));
    }

    /**
     * Filter active variants
     */
    public static Specification<ProductVariant> isActive() {
        return hasStatus(ProductVariant.Status.ACTIVE);
    }

    /**
     * Filter available variants (active and in stock)
     */
    public static Specification<ProductVariant> isAvailable() {
        return Specification.where(isActive()).and(isInStock());
    }

    /**
     * Filter variants with specific attribute
     */
    public static Specification<ProductVariant> hasAttribute(String attributeName) {
        return (root, query, criteriaBuilder) -> {
            if (attributeName == null || attributeName.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.isNotNull(root.get("attributes").get(attributeName));
        };
    }

    /**
     * Filter variants with specific attribute value
     */
    public static Specification<ProductVariant> hasAttributeValue(String attributeName, String attributeValue) {
        return (root, query, criteriaBuilder) -> {
            if (attributeName == null || attributeName.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            if (attributeValue == null) {
                return criteriaBuilder.isNull(root.get("attributes").get(attributeName));
            }
            return criteriaBuilder.equal(root.get("attributes").get(attributeName), attributeValue);
        };
    }

    /**
     * Filter variants with attribute value containing text
     */
    public static Specification<ProductVariant> hasAttributeValueContaining(String attributeName,
            String valueContains) {
        return (root, query, criteriaBuilder) -> {
            if (attributeName == null || attributeName.trim().isEmpty() ||
                    valueContains == null || valueContains.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("attributes").get(attributeName)),
                    "%" + valueContains.toLowerCase() + "%");
        };
    }

    /**
     * Filter variants with any attributes
     */
    public static Specification<ProductVariant> hasAttributes() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(
                criteriaBuilder.size(root.get("attributes")), 0);
    }

    /**
     * Filter variants without attributes
     */
    public static Specification<ProductVariant> hasNoAttributes() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(
                criteriaBuilder.size(root.get("attributes")), 0);
    }

    /**
     * Filter variants with SKU
     */
    public static Specification<ProductVariant> hasSku() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.isNotNull(root.get("sku")),
                criteriaBuilder.notEqual(root.get("sku"), ""));
    }

    /**
     * Filter variants without SKU
     */
    public static Specification<ProductVariant> hasNoSku() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.isNull(root.get("sku")),
                criteriaBuilder.equal(root.get("sku"), ""));
    }

    /**
     * Complex filter for product variants
     */
    public static Specification<ProductVariant> withFilters(
            UUID productId,
            String variantName,
            String sku,
            ProductVariant.Status status,
            BigDecimal minPriceOverride,
            BigDecimal maxPriceOverride,
            Long minStock,
            Long maxStock,
            Boolean inStock,
            Boolean hasPriceOverride,
            String attributeName,
            String attributeValue) {

        return Specification.where(belongsToProductId(productId))
                .and(hasNameContaining(variantName))
                .and(hasSkuContaining(sku))
                .and(hasStatus(status))
                .and(hasPriceOverrideBetween(minPriceOverride, maxPriceOverride))
                .and(hasStockBetween(minStock, maxStock))
                .and(inStock != null && inStock ? isInStock() : null)
                .and(inStock != null && !inStock ? isOutOfStock() : null)
                .and(hasPriceOverride != null && hasPriceOverride ? hasPriceOverride() : null)
                .and(hasPriceOverride != null && !hasPriceOverride ? hasNoPriceOverride() : null)
                .and(hasAttributeValue(attributeName, attributeValue));
    }

    /**
     * Filter for finding variants that need SKU assignment
     */
    public static Specification<ProductVariant> needsSku() {
        return Specification.where(hasNoSku()).and(isActive());
    }

    /**
     * Filter for finding variants with duplicate SKUs
     */
    public static Specification<ProductVariant> hasDuplicateSku() {
        return (root, query, criteriaBuilder) -> {
            // This is a complex query that would typically be handled at the service level
            // For now, return a conjunction as this would need a subquery
            return criteriaBuilder.conjunction();
        };
    }

    /**
     * Filter for finding discontinued variants that still have stock
     */
    public static Specification<ProductVariant> isDiscontinuedWithStock() {
        return Specification.where(hasStatus(ProductVariant.Status.DISCONTINUED)).and(isInStock());
    }

    /**
     * Filter variants by effective price range (considering base product price)
     */
    public static Specification<ProductVariant> hasEffectivePriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // This is a simplified version - in reality, you'd need to join with Product
            // and calculate the effective price (priceOverride > 0 ? priceOverride :
            // product.price)

            if (minPrice != null) {
                Predicate overridePrice = criteriaBuilder.and(
                        criteriaBuilder.greaterThan(root.get("priceOverride"), BigDecimal.ZERO),
                        criteriaBuilder.greaterThanOrEqualTo(root.get("priceOverride"), minPrice));

                Predicate basePrice = criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("priceOverride"), BigDecimal.ZERO),
                        criteriaBuilder.greaterThanOrEqualTo(root.get("product").get("price"), minPrice));

                predicates.add(criteriaBuilder.or(overridePrice, basePrice));
            }

            if (maxPrice != null) {
                Predicate overridePrice = criteriaBuilder.and(
                        criteriaBuilder.greaterThan(root.get("priceOverride"), BigDecimal.ZERO),
                        criteriaBuilder.lessThanOrEqualTo(root.get("priceOverride"), maxPrice));

                Predicate basePrice = criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("priceOverride"), BigDecimal.ZERO),
                        criteriaBuilder.lessThanOrEqualTo(root.get("product").get("price"), maxPrice));

                predicates.add(criteriaBuilder.or(overridePrice, basePrice));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}