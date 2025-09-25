package com.example.ecommerce.catalog.web.dto.product;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class ProductResponseDTO {
    private final UUID id;
    private final String productName;
    private final String description;
    private final String brandName;
    private final BigDecimal price;
    private final BigDecimal weight;
    private final BigDecimal rating;
    private final long stockQuantity;
    private final String status;
    private final CategorySummary category;
    private final boolean isInStock;
    private final boolean isActive;
    private final boolean isAvailable;
    private final boolean hasVariants;
    private final boolean hasPrimaryImage;
    private final long totalStock;
    private final BigDecimal lowestPrice;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<ProductImageResponseDTO> productImages;
    private final List<ProductVariantResponseDTO> productVariants;

    public record CategorySummary(UUID id, String name, String slug) {
    }

    public ProductResponseDTO(UUID id, String productName, String description, String brandName,
            BigDecimal price, BigDecimal weight, BigDecimal rating, long stockQuantity,
            String status, CategorySummary category, boolean isInStock, boolean isActive,
            boolean isAvailable, boolean hasVariants, boolean hasPrimaryImage,
            long totalStock, BigDecimal lowestPrice, LocalDateTime createdAt,
            LocalDateTime updatedAt, List<ProductImageResponseDTO> productImages,
            List<ProductVariantResponseDTO> productVariants) {
        this.id = id;
        this.productName = productName;
        this.description = description;
        this.brandName = brandName;
        this.price = price;
        this.weight = weight;
        this.rating = rating;
        this.stockQuantity = stockQuantity;
        this.status = status;
        this.category = category;
        this.isInStock = isInStock;
        this.isActive = isActive;
        this.isAvailable = isAvailable;
        this.hasVariants = hasVariants;
        this.hasPrimaryImage = hasPrimaryImage;
        this.totalStock = totalStock;
        this.lowestPrice = lowestPrice;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.productImages = productImages;
        this.productVariants = productVariants;
    }
}