package com.example.ecommerce.catalog.infra;

import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.domain.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {

    /**
     * Find all variants for a specific product
     */
    List<ProductVariant> findByProduct(Product product);

    /**
     * Find all variants for a specific product by product ID
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id = :productId")
    List<ProductVariant> findByProductId(@Param("productId") UUID productId);

    /**
     * Find variants by status
     */
    List<ProductVariant> findByStatus(ProductVariant.Status status);

    /**
     * Find active variants for a product
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id = :productId AND pv.status = 'ACTIVE'")
    List<ProductVariant> findActiveVariantsByProductId(@Param("productId") UUID productId);

    /**
     * Find variants with stock greater than specified amount
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.stockQuantity > :minStock")
    List<ProductVariant> findByStockQuantityGreaterThan(@Param("minStock") long minStock);

    /**
     * Find in-stock variants for a product
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id = :productId AND pv.stockQuantity > 0")
    List<ProductVariant> findInStockVariantsByProductId(@Param("productId") UUID productId);

    /**
     * Find available variants (active and in stock) for a product
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id = :productId AND pv.status = 'ACTIVE' AND pv.stockQuantity > 0")
    List<ProductVariant> findAvailableVariantsByProductId(@Param("productId") UUID productId);

    /**
     * Find variant by SKU
     */
    Optional<ProductVariant> findBySku(String sku);

    /**
     * Check if SKU exists
     */
    boolean existsBySku(String sku);

    /**
     * Find variant by name within a product
     */
    Optional<ProductVariant> findByProductAndVariantName(Product product, String variantName);

    /**
     * Find variant by name and product ID
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id = :productId AND pv.variantName = :variantName")
    Optional<ProductVariant> findByProductIdAndVariantName(@Param("productId") UUID productId,
            @Param("variantName") String variantName);

    /**
     * Check if variant name exists for a product
     */
    @Query("SELECT COUNT(pv) > 0 FROM ProductVariant pv WHERE pv.product.id = :productId AND pv.variantName = :variantName")
    boolean existsByProductIdAndVariantName(@Param("productId") UUID productId,
            @Param("variantName") String variantName);

    /**
     * Find variants with price override
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.priceOverride > 0")
    List<ProductVariant> findVariantsWithPriceOverride();

    /**
     * Find variants with price override within range
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.priceOverride BETWEEN :minPrice AND :maxPrice")
    List<ProductVariant> findByPriceOverrideBetween(@Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice);

    /**
     * Count variants for a product
     */
    long countByProduct(Product product);

    /**
     * Count variants for a product by product ID
     */
    @Query("SELECT COUNT(pv) FROM ProductVariant pv WHERE pv.product.id = :productId")
    long countByProductId(@Param("productId") UUID productId);

    /**
     * Count active variants for a product
     */
    @Query("SELECT COUNT(pv) FROM ProductVariant pv WHERE pv.product.id = :productId AND pv.status = 'ACTIVE'")
    long countActiveVariantsByProductId(@Param("productId") UUID productId);

    /**
     * Get total stock for all variants of a product
     */
    @Query("SELECT COALESCE(SUM(pv.stockQuantity), 0) FROM ProductVariant pv WHERE pv.product.id = :productId")
    long getTotalStockByProductId(@Param("productId") UUID productId);

    /**
     * Get total available stock (active variants only) for a product
     */
    @Query("SELECT COALESCE(SUM(pv.stockQuantity), 0) FROM ProductVariant pv WHERE pv.product.id = :productId AND pv.status = 'ACTIVE'")
    long getTotalAvailableStockByProductId(@Param("productId") UUID productId);

    /**
     * Find variants by attribute
     */
    @Query("SELECT pv FROM ProductVariant pv JOIN pv.attributes attr WHERE KEY(attr) = :attributeName AND VALUE(attr) = :attributeValue")
    List<ProductVariant> findByAttribute(@Param("attributeName") String attributeName,
            @Param("attributeValue") String attributeValue);

    /**
     * Find variants by attribute name (regardless of value)
     */
    @Query("SELECT pv FROM ProductVariant pv JOIN pv.attributes attr WHERE KEY(attr) = :attributeName")
    List<ProductVariant> findByAttributeName(@Param("attributeName") String attributeName);

    /**
     * Find variants with low stock (below threshold)
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.stockQuantity <= :threshold AND pv.status = 'ACTIVE'")
    List<ProductVariant> findLowStockVariants(@Param("threshold") long threshold);

    /**
     * Find out of stock variants
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.stockQuantity = 0")
    List<ProductVariant> findOutOfStockVariants();

    /**
     * Delete all variants for a specific product
     */
    void deleteByProduct(Product product);

    /**
     * Delete all variants for a specific product by product ID
     */
    @Query("DELETE FROM ProductVariant pv WHERE pv.product.id = :productId")
    void deleteByProductId(@Param("productId") UUID productId);
}