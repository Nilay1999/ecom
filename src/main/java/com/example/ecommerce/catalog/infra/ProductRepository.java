package com.example.ecommerce.catalog.infra;

import com.example.ecommerce.catalog.domain.Category;
import com.example.ecommerce.catalog.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    /**
     * Find products by name (case-insensitive partial match)
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> findByProductNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Find products by brand name
     */
    List<Product> findByBrandName(String brandName);

    /**
     * Find products by brand name (case-insensitive partial match)
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.brandName) LIKE LOWER(CONCAT('%', :brandName, '%'))")
    List<Product> findByBrandNameContainingIgnoreCase(@Param("brandName") String brandName);

    /**
     * Find products by category
     */
    List<Product> findByCategory(Category category);

    /**
     * Find products by category ID
     */
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
    List<Product> findByCategoryId(@Param("categoryId") UUID categoryId);

    /**
     * Find products by status
     */
    List<Product> findByStatus(Product.Status status);

    /**
     * Find active products
     */
    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE'")
    List<Product> findActiveProducts();

    /**
     * Find products with stock greater than specified amount
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity > :minStock")
    List<Product> findByStockQuantityGreaterThan(@Param("minStock") long minStock);

    /**
     * Find in-stock products
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0")
    List<Product> findInStockProducts();

    /**
     * Find available products (active and in stock)
     */
    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' AND p.stockQuantity > 0")
    List<Product> findAvailableProducts();

    /**
     * Find products by price range
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceBetween(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    /**
     * Find products by rating range
     */
    @Query("SELECT p FROM Product p WHERE p.rating BETWEEN :minRating AND :maxRating")
    List<Product> findByRatingBetween(@Param("minRating") BigDecimal minRating,
            @Param("maxRating") BigDecimal maxRating);

    /**
     * Find products with rating greater than or equal to specified value
     */
    @Query("SELECT p FROM Product p WHERE p.rating >= :minRating")
    List<Product> findByRatingGreaterThanEqual(@Param("minRating") BigDecimal minRating);

    /**
     * Search products by name or description (full-text search)
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Product> searchByNameOrDescription(@Param("searchTerm") String searchTerm);

    /**
     * Search products with pagination
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Product> searchByNameOrDescription(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find products by category and status
     */
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.status = :status")
    List<Product> findByCategoryIdAndStatus(@Param("categoryId") UUID categoryId,
            @Param("status") Product.Status status);

    /**
     * Find products by multiple categories (including subcategories)
     */
    @Query("SELECT p FROM Product p WHERE p.category IN :categories")
    List<Product> findByCategoryIn(@Param("categories") List<Category> categories);

    /**
     * Count products by category
     */
    long countByCategory(Category category);

    /**
     * Count products by category ID
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
    long countByCategoryId(@Param("categoryId") UUID categoryId);

    /**
     * Count active products by category
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId AND p.status = 'ACTIVE'")
    long countActiveByCategoryId(@Param("categoryId") UUID categoryId);

    /**
     * Find products with low stock (below threshold)
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= :threshold AND p.status = 'ACTIVE'")
    List<Product> findLowStockProducts(@Param("threshold") long threshold);

    /**
     * Find out of stock products
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity = 0")
    List<Product> findOutOfStockProducts();

    /**
     * Find top-rated products
     */
    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' ORDER BY p.rating DESC")
    List<Product> findTopRatedProducts(Pageable pageable);

    /**
     * Find newest products
     */
    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' ORDER BY p.createdAt DESC")
    List<Product> findNewestProducts(Pageable pageable);

    /**
     * Find products by weight range
     */
    @Query("SELECT p FROM Product p WHERE p.weight BETWEEN :minWeight AND :maxWeight")
    List<Product> findByWeightBetween(@Param("minWeight") BigDecimal minWeight,
            @Param("maxWeight") BigDecimal maxWeight);

    /**
     * Check if product name exists
     */
    boolean existsByProductName(String productName);

    /**
     * Check if product name exists for different product (for updates)
     */
    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.productName = :productName AND p.id != :productId")
    boolean existsByProductNameAndIdNot(@Param("productName") String productName, @Param("productId") UUID productId);

    /**
     * Find products that have variants
     */
    @Query("SELECT DISTINCT p FROM Product p WHERE SIZE(p.productVariants) > 0")
    List<Product> findProductsWithVariants();

    /**
     * Find products that have images
     */
    @Query("SELECT DISTINCT p FROM Product p WHERE SIZE(p.productImages) > 0")
    List<Product> findProductsWithImages();

    /**
     * Find products without primary image
     */
    @Query("SELECT p FROM Product p WHERE p.id NOT IN (SELECT DISTINCT pi.product.id FROM ProductImage pi WHERE pi.isPrimary = true)")
    List<Product> findProductsWithoutPrimaryImage();
}
