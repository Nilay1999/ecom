package com.example.ecommerce.catalog.infra;

import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.domain.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {

    /**
     * Find all images for a specific product, ordered by display order
     */
    List<ProductImage> findByProductOrderByDisplayOrderAsc(Product product);

    /**
     * Find all images for a specific product by product ID, ordered by display
     * order
     */
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = :productId ORDER BY pi.displayOrder ASC")
    List<ProductImage> findByProductIdOrderByDisplayOrderAsc(@Param("productId") UUID productId);

    /**
     * Find the primary image for a specific product
     */
    Optional<ProductImage> findByProductAndIsPrimaryTrue(Product product);

    /**
     * Find the primary image for a specific product by product ID
     */
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = :productId AND pi.isPrimary = true")
    Optional<ProductImage> findPrimaryImageByProductId(@Param("productId") UUID productId);

    /**
     * Find all primary images (for validation purposes)
     */
    List<ProductImage> findByIsPrimaryTrue();

    /**
     * Count images for a specific product
     */
    long countByProduct(Product product);

    /**
     * Count images for a specific product by product ID
     */
    @Query("SELECT COUNT(pi) FROM ProductImage pi WHERE pi.product.id = :productId")
    long countByProductId(@Param("productId") UUID productId);

    /**
     * Find images by display order range for a product
     */
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = :productId AND pi.displayOrder BETWEEN :minOrder AND :maxOrder ORDER BY pi.displayOrder ASC")
    List<ProductImage> findByProductIdAndDisplayOrderBetween(
            @Param("productId") UUID productId,
            @Param("minOrder") Integer minOrder,
            @Param("maxOrder") Integer maxOrder);

    /**
     * Find the maximum display order for a product (useful for adding new images)
     */
    @Query("SELECT COALESCE(MAX(pi.displayOrder), 0) FROM ProductImage pi WHERE pi.product.id = :productId")
    Integer findMaxDisplayOrderByProductId(@Param("productId") UUID productId);

    /**
     * Check if a product has a primary image
     */
    @Query("SELECT COUNT(pi) > 0 FROM ProductImage pi WHERE pi.product.id = :productId AND pi.isPrimary = true")
    boolean existsPrimaryImageByProductId(@Param("productId") UUID productId);

    /**
     * Find images with specific alt text (for search purposes)
     */
    @Query("SELECT pi FROM ProductImage pi WHERE pi.altText LIKE %:altText% ORDER BY pi.displayOrder ASC")
    List<ProductImage> findByAltTextContainingIgnoreCase(@Param("altText") String altText);

    /**
     * Delete all images for a specific product
     */
    void deleteByProduct(Product product);

    /**
     * Delete all images for a specific product by product ID
     */
    @Query("DELETE FROM ProductImage pi WHERE pi.product.id = :productId")
    void deleteByProductId(@Param("productId") UUID productId);
}