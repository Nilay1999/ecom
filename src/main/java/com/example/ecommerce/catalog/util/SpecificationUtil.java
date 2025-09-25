package com.example.ecommerce.catalog.util;

import com.example.ecommerce.catalog.domain.Category;
import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.domain.ProductImage;
import com.example.ecommerce.catalog.domain.ProductVariant;
import com.example.ecommerce.catalog.domain.specification.CategorySpecification;
import com.example.ecommerce.catalog.domain.specification.ProductImageSpecification;
import com.example.ecommerce.catalog.domain.specification.ProductSpecification;
import com.example.ecommerce.catalog.domain.specification.ProductVariantSpecification;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Utility class that provides convenient methods for building complex
 * specifications
 * and common query patterns across the catalog domain.
 */
public final class SpecificationUtil {

    private SpecificationUtil() {
        // Utility class - prevent instantiation
    }

    // Product specification builders

    /**
     * Builds a comprehensive product search specification
     */
    public static Specification<Product> buildProductSearchSpec(ProductSearchCriteria criteria) {
        return ProductSpecification.withAdvancedFilters(
                criteria.getSearchTerm(),
                criteria.getBrands(),
                criteria.getCategoryIds(),
                criteria.getStatuses(),
                criteria.getMinPrice(),
                criteria.getMaxPrice(),
                criteria.getMinRating(),
                criteria.getMinStock(),
                criteria.getMaxStock(),
                criteria.getHasVariants(),
                criteria.getHasPrimaryImage(),
                criteria.getCreatedAfter(),
                criteria.getUpdatedAfter());
    }

    /**
     * Builds a specification for finding products that need attention
     */
    public static Specification<Product> buildProductsNeedingAttention() {
        return Specification.where(ProductSpecification.needsRestock(10))
                .or(ProductSpecification.hasNoPrimaryImage())
                .or(ProductSpecification.hasStatus(Product.Status.OUT_OF_STOCK));
    }

    /**
     * Builds a specification for inventory management queries
     */
    public static Specification<Product> buildInventoryManagementSpec(
            Long lowStockThreshold,
            List<Product.Status> statuses,
            List<String> brands) {

        return Specification.where(ProductSpecification.hasLowStock(lowStockThreshold != null ? lowStockThreshold : 10))
                .and(ProductSpecification.hasStatusIn(statuses))
                .and(ProductSpecification.hasBrandIn(brands));
    }

    // Category specification builders

    /**
     * Builds a comprehensive category search specification
     */
    public static Specification<Category> buildCategorySearchSpec(CategorySearchCriteria criteria) {
        return CategorySpecification.withAdvancedFilters(
                criteria.getSearchTerm(),
                criteria.getParentId(),
                criteria.getIsRoot(),
                criteria.getHasSubcategories(),
                criteria.getHasProducts(),
                criteria.getMinProductCount(),
                criteria.getMaxProductCount(),
                criteria.getDepth(),
                criteria.getCreatedAfter(),
                criteria.getUpdatedAfter(),
                criteria.getProductStatus());
    }

    /**
     * Builds a specification for category hierarchy navigation
     */
    public static Specification<Category> buildCategoryHierarchySpec(UUID rootCategoryId, int maxDepth) {
        Specification<Category> spec = Specification.where(CategorySpecification.hasParentId(rootCategoryId));

        if (maxDepth > 0) {
            spec = spec.and(CategorySpecification.hasDepth(maxDepth));
        }

        return spec;
    }

    /**
     * Builds a specification for finding categories that need cleanup
     */
    public static Specification<Category> buildCategoriesNeedingCleanup() {
        return CategorySpecification.needsAttention();
    }

    // Product variant specification builders

    /**
     * Builds a comprehensive product variant search specification
     */
    public static Specification<ProductVariant> buildVariantSearchSpec(VariantSearchCriteria criteria) {
        return ProductVariantSpecification.withFilters(
                criteria.getProductId(),
                criteria.getVariantName(),
                criteria.getSku(),
                criteria.getStatus(),
                criteria.getMinPriceOverride(),
                criteria.getMaxPriceOverride(),
                criteria.getMinStock(),
                criteria.getMaxStock(),
                criteria.getInStock(),
                criteria.getHasPriceOverride(),
                criteria.getAttributeName(),
                criteria.getAttributeValue());
    }

    /**
     * Builds a specification for variant inventory management
     */
    public static Specification<ProductVariant> buildVariantInventorySpec(
            Long lowStockThreshold,
            List<ProductVariant.Status> statuses) {

        return Specification
                .where(ProductVariantSpecification.hasLowStock(lowStockThreshold != null ? lowStockThreshold : 5))
                .and(ProductVariantSpecification.hasStatusIn(statuses));
    }

    // Product image specification builders

    /**
     * Builds a comprehensive product image search specification
     */
    public static Specification<ProductImage> buildImageSearchSpec(ImageSearchCriteria criteria) {
        return ProductImageSpecification.withFilters(
                criteria.getProductId(),
                criteria.getIsPrimary(),
                criteria.getUrlContains(),
                criteria.getAltTextContains(),
                criteria.getMinDisplayOrder(),
                criteria.getMaxDisplayOrder(),
                criteria.getImageFormats());
    }

    /**
     * Builds a specification for finding images that need attention
     */
    public static Specification<ProductImage> buildImagesNeedingAttention() {
        return ProductImageSpecification.needsAltText()
                .or(ProductImageSpecification.isOrphaned());
    }

    // Common sorting utilities

    /**
     * Creates common sort orders for products
     */
    public static Sort getProductSort(String sortBy, String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;

        return switch (sortBy != null ? sortBy.toLowerCase() : "name") {
            case "price" -> Sort.by(sortDirection, "price");
            case "rating" -> Sort.by(sortDirection, "rating");
            case "stock" -> Sort.by(sortDirection, "stockQuantity");
            case "created" -> Sort.by(sortDirection, "createdAt");
            case "updated" -> Sort.by(sortDirection, "updatedAt");
            case "brand" -> Sort.by(sortDirection, "brandName");
            default -> Sort.by(sortDirection, "productName");
        };
    }

    /**
     * Creates common sort orders for categories
     */
    public static Sort getCategorySort(String sortBy, String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;

        return switch (sortBy != null ? sortBy.toLowerCase() : "name") {
            case "products" -> Sort.by(sortDirection, "products.size");
            case "created" -> Sort.by(sortDirection, "createdAt");
            case "updated" -> Sort.by(sortDirection, "updatedAt");
            default -> Sort.by(sortDirection, "name");
        };
    }

    /**
     * Creates common sort orders for variants
     */
    public static Sort getVariantSort(String sortBy, String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;

        return switch (sortBy != null ? sortBy.toLowerCase() : "name") {
            case "price" -> Sort.by(sortDirection, "priceOverride");
            case "stock" -> Sort.by(sortDirection, "stockQuantity");
            case "sku" -> Sort.by(sortDirection, "sku");
            case "created" -> Sort.by(sortDirection, "createdAt");
            default -> Sort.by(sortDirection, "variantName");
        };
    }

    // Search criteria classes

    public static class ProductSearchCriteria {
        private String searchTerm;
        private List<String> brands;
        private List<UUID> categoryIds;
        private List<Product.Status> statuses;
        private BigDecimal minPrice;
        private BigDecimal maxPrice;
        private BigDecimal minRating;
        private Long minStock;
        private Long maxStock;
        private Boolean hasVariants;
        private Boolean hasPrimaryImage;
        private LocalDateTime createdAfter;
        private LocalDateTime updatedAfter;

        // Getters and setters
        public String getSearchTerm() {
            return searchTerm;
        }

        public void setSearchTerm(String searchTerm) {
            this.searchTerm = searchTerm;
        }

        public List<String> getBrands() {
            return brands;
        }

        public void setBrands(List<String> brands) {
            this.brands = brands;
        }

        public List<UUID> getCategoryIds() {
            return categoryIds;
        }

        public void setCategoryIds(List<UUID> categoryIds) {
            this.categoryIds = categoryIds;
        }

        public List<Product.Status> getStatuses() {
            return statuses;
        }

        public void setStatuses(List<Product.Status> statuses) {
            this.statuses = statuses;
        }

        public BigDecimal getMinPrice() {
            return minPrice;
        }

        public void setMinPrice(BigDecimal minPrice) {
            this.minPrice = minPrice;
        }

        public BigDecimal getMaxPrice() {
            return maxPrice;
        }

        public void setMaxPrice(BigDecimal maxPrice) {
            this.maxPrice = maxPrice;
        }

        public BigDecimal getMinRating() {
            return minRating;
        }

        public void setMinRating(BigDecimal minRating) {
            this.minRating = minRating;
        }

        public Long getMinStock() {
            return minStock;
        }

        public void setMinStock(Long minStock) {
            this.minStock = minStock;
        }

        public Long getMaxStock() {
            return maxStock;
        }

        public void setMaxStock(Long maxStock) {
            this.maxStock = maxStock;
        }

        public Boolean getHasVariants() {
            return hasVariants;
        }

        public void setHasVariants(Boolean hasVariants) {
            this.hasVariants = hasVariants;
        }

        public Boolean getHasPrimaryImage() {
            return hasPrimaryImage;
        }

        public void setHasPrimaryImage(Boolean hasPrimaryImage) {
            this.hasPrimaryImage = hasPrimaryImage;
        }

        public LocalDateTime getCreatedAfter() {
            return createdAfter;
        }

        public void setCreatedAfter(LocalDateTime createdAfter) {
            this.createdAfter = createdAfter;
        }

        public LocalDateTime getUpdatedAfter() {
            return updatedAfter;
        }

        public void setUpdatedAfter(LocalDateTime updatedAfter) {
            this.updatedAfter = updatedAfter;
        }
    }

    public static class CategorySearchCriteria {
        private String searchTerm;
        private UUID parentId;
        private Boolean isRoot;
        private Boolean hasSubcategories;
        private Boolean hasProducts;
        private Integer minProductCount;
        private Integer maxProductCount;
        private Integer depth;
        private LocalDateTime createdAfter;
        private LocalDateTime updatedAfter;
        private Product.Status productStatus;

        // Getters and setters
        public String getSearchTerm() {
            return searchTerm;
        }

        public void setSearchTerm(String searchTerm) {
            this.searchTerm = searchTerm;
        }

        public UUID getParentId() {
            return parentId;
        }

        public void setParentId(UUID parentId) {
            this.parentId = parentId;
        }

        public Boolean getIsRoot() {
            return isRoot;
        }

        public void setIsRoot(Boolean isRoot) {
            this.isRoot = isRoot;
        }

        public Boolean getHasSubcategories() {
            return hasSubcategories;
        }

        public void setHasSubcategories(Boolean hasSubcategories) {
            this.hasSubcategories = hasSubcategories;
        }

        public Boolean getHasProducts() {
            return hasProducts;
        }

        public void setHasProducts(Boolean hasProducts) {
            this.hasProducts = hasProducts;
        }

        public Integer getMinProductCount() {
            return minProductCount;
        }

        public void setMinProductCount(Integer minProductCount) {
            this.minProductCount = minProductCount;
        }

        public Integer getMaxProductCount() {
            return maxProductCount;
        }

        public void setMaxProductCount(Integer maxProductCount) {
            this.maxProductCount = maxProductCount;
        }

        public Integer getDepth() {
            return depth;
        }

        public void setDepth(Integer depth) {
            this.depth = depth;
        }

        public LocalDateTime getCreatedAfter() {
            return createdAfter;
        }

        public void setCreatedAfter(LocalDateTime createdAfter) {
            this.createdAfter = createdAfter;
        }

        public LocalDateTime getUpdatedAfter() {
            return updatedAfter;
        }

        public void setUpdatedAfter(LocalDateTime updatedAfter) {
            this.updatedAfter = updatedAfter;
        }

        public Product.Status getProductStatus() {
            return productStatus;
        }

        public void setProductStatus(Product.Status productStatus) {
            this.productStatus = productStatus;
        }
    }

    public static class VariantSearchCriteria {
        private UUID productId;
        private String variantName;
        private String sku;
        private ProductVariant.Status status;
        private BigDecimal minPriceOverride;
        private BigDecimal maxPriceOverride;
        private Long minStock;
        private Long maxStock;
        private Boolean inStock;
        private Boolean hasPriceOverride;
        private String attributeName;
        private String attributeValue;

        // Getters and setters
        public UUID getProductId() {
            return productId;
        }

        public void setProductId(UUID productId) {
            this.productId = productId;
        }

        public String getVariantName() {
            return variantName;
        }

        public void setVariantName(String variantName) {
            this.variantName = variantName;
        }

        public String getSku() {
            return sku;
        }

        public void setSku(String sku) {
            this.sku = sku;
        }

        public ProductVariant.Status getStatus() {
            return status;
        }

        public void setStatus(ProductVariant.Status status) {
            this.status = status;
        }

        public BigDecimal getMinPriceOverride() {
            return minPriceOverride;
        }

        public void setMinPriceOverride(BigDecimal minPriceOverride) {
            this.minPriceOverride = minPriceOverride;
        }

        public BigDecimal getMaxPriceOverride() {
            return maxPriceOverride;
        }

        public void setMaxPriceOverride(BigDecimal maxPriceOverride) {
            this.maxPriceOverride = maxPriceOverride;
        }

        public Long getMinStock() {
            return minStock;
        }

        public void setMinStock(Long minStock) {
            this.minStock = minStock;
        }

        public Long getMaxStock() {
            return maxStock;
        }

        public void setMaxStock(Long maxStock) {
            this.maxStock = maxStock;
        }

        public Boolean getInStock() {
            return inStock;
        }

        public void setInStock(Boolean inStock) {
            this.inStock = inStock;
        }

        public Boolean getHasPriceOverride() {
            return hasPriceOverride;
        }

        public void setHasPriceOverride(Boolean hasPriceOverride) {
            this.hasPriceOverride = hasPriceOverride;
        }

        public String getAttributeName() {
            return attributeName;
        }

        public void setAttributeName(String attributeName) {
            this.attributeName = attributeName;
        }

        public String getAttributeValue() {
            return attributeValue;
        }

        public void setAttributeValue(String attributeValue) {
            this.attributeValue = attributeValue;
        }
    }

    public static class ImageSearchCriteria {
        private UUID productId;
        private Boolean isPrimary;
        private String urlContains;
        private String altTextContains;
        private Integer minDisplayOrder;
        private Integer maxDisplayOrder;
        private List<String> imageFormats;

        // Getters and setters
        public UUID getProductId() {
            return productId;
        }

        public void setProductId(UUID productId) {
            this.productId = productId;
        }

        public Boolean getIsPrimary() {
            return isPrimary;
        }

        public void setIsPrimary(Boolean isPrimary) {
            this.isPrimary = isPrimary;
        }

        public String getUrlContains() {
            return urlContains;
        }

        public void setUrlContains(String urlContains) {
            this.urlContains = urlContains;
        }

        public String getAltTextContains() {
            return altTextContains;
        }

        public void setAltTextContains(String altTextContains) {
            this.altTextContains = altTextContains;
        }

        public Integer getMinDisplayOrder() {
            return minDisplayOrder;
        }

        public void setMinDisplayOrder(Integer minDisplayOrder) {
            this.minDisplayOrder = minDisplayOrder;
        }

        public Integer getMaxDisplayOrder() {
            return maxDisplayOrder;
        }

        public void setMaxDisplayOrder(Integer maxDisplayOrder) {
            this.maxDisplayOrder = maxDisplayOrder;
        }

        public List<String> getImageFormats() {
            return imageFormats;
        }

        public void setImageFormats(List<String> imageFormats) {
            this.imageFormats = imageFormats;
        }
    }
}