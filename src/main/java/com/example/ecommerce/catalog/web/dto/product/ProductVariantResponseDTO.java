package com.example.ecommerce.catalog.web.dto.product;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
public class ProductVariantResponseDTO {
    private final UUID id;
    private final UUID productId;
    private final String variantName;
    private final BigDecimal priceOverride;
    private final BigDecimal effectivePrice;
    private final boolean hasPriceOverride;
    private final long stockQuantity;
    private final Map<String, String> attributes;
    private final String status;
    private final String sku;
    private final boolean isInStock;
    private final boolean isActive;
    private final boolean isAvailable;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ProductVariantResponseDTO(UUID id, UUID productId, String variantName, BigDecimal priceOverride,
            BigDecimal effectivePrice, boolean hasPriceOverride, long stockQuantity,
            Map<String, String> attributes, String status, String sku,
            boolean isInStock, boolean isActive, boolean isAvailable,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.productId = productId;
        this.variantName = variantName;
        this.priceOverride = priceOverride;
        this.effectivePrice = effectivePrice;
        this.hasPriceOverride = hasPriceOverride;
        this.stockQuantity = stockQuantity;
        this.attributes = attributes;
        this.status = status;
        this.sku = sku;
        this.isInStock = isInStock;
        this.isActive = isActive;
        this.isAvailable = isAvailable;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}