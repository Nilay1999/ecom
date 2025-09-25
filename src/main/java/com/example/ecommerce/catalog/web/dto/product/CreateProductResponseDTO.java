package com.example.ecommerce.catalog.web.dto.product;

import com.example.ecommerce.catalog.domain.Category;
import com.example.ecommerce.catalog.domain.ProductImage;
import com.example.ecommerce.catalog.domain.ProductVariant;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class CreateProductResponseDTO {
    private final UUID id;
    private final String productName;
    private final String description;
    private final String brandName;
    private final BigDecimal price;
    private final BigDecimal weight;
    private final BigDecimal rating;
    private final long stockQuantity;
    private final String status;
    private final String categoryName;
    private final UUID categoryId;
    private final boolean isInStock;
    private final boolean isActive;
    private final boolean isAvailable;
    private final boolean hasVariants;
    private final boolean hasPrimaryImage;
    private final long totalStock;
    private final BigDecimal lowestPrice;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private List<ProductImageRes> productImageList;
    private List<ProductVariantSummary> productVariants;

    public record ProductImageRes(UUID id, String imageUrl, boolean isPrimary, Integer displayOrder, String altText) {
    }

    public record ProductVariantSummary(UUID id, String variantName, BigDecimal effectivePrice, long stockQuantity,
            String status) {
    }

    public CreateProductResponseDTO(UUID id, String productName, String brandName, String description,
            BigDecimal price, BigDecimal weight, BigDecimal rating, long stockQuantity,
            String status, Category category, List<ProductImage> productImages,
            List<ProductVariant> productVariants, boolean isInStock, boolean isActive,
            boolean isAvailable, boolean hasVariants, boolean hasPrimaryImage,
            long totalStock, BigDecimal lowestPrice, LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.productName = productName;
        this.brandName = brandName;
        this.description = description;
        this.price = price;
        this.weight = weight;
        this.rating = rating;
        this.stockQuantity = stockQuantity;
        this.status = status;
        this.categoryName = category.getName();
        this.categoryId = category.getId();
        this.isInStock = isInStock;
        this.isActive = isActive;
        this.isAvailable = isAvailable;
        this.hasVariants = hasVariants;
        this.hasPrimaryImage = hasPrimaryImage;
        this.totalStock = totalStock;
        this.lowestPrice = lowestPrice;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

        this.productImageList = new ArrayList<>();
        for (ProductImage image : productImages) {
            ProductImageRes imageRes = new ProductImageRes(image.getId(), image.getImageUrl(),
                    image.isPrimary(), image.getDisplayOrder(),
                    image.getAltText());
            productImageList.add(imageRes);
        }

        this.productVariants = new ArrayList<>();
        for (ProductVariant variant : productVariants) {
            ProductVariantSummary variantSummary = new ProductVariantSummary(variant.getId(),
                    variant.getVariantName(),
                    variant.getEffectivePrice(),
                    variant.getStockQuantity(),
                    variant.getStatus().name());
            this.productVariants.add(variantSummary);
        }
    }

    // Backward compatibility constructor
    public CreateProductResponseDTO(UUID id, String brandName, String description, BigDecimal price, BigDecimal weight,
            long stockQuantity, Category category) {
        this.id = id;
        this.productName = null; // Will need to be set separately for backward compatibility
        this.brandName = brandName;
        this.description = description;
        this.price = price;
        this.weight = weight;
        this.rating = BigDecimal.ONE;
        this.stockQuantity = stockQuantity;
        this.status = "OUT_OF_STOCK";
        this.categoryName = category.getName();
        this.categoryId = category.getId();
        this.isInStock = stockQuantity > 0;
        this.isActive = false;
        this.isAvailable = false;
        this.hasVariants = false;
        this.hasPrimaryImage = false;
        this.totalStock = stockQuantity;
        this.lowestPrice = price;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.productImageList = new ArrayList<>();
        this.productVariants = new ArrayList<>();
    }
}
