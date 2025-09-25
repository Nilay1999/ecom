package com.example.ecommerce.catalog.web.dto.product;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ProductImageResponseDTO {
    private final UUID id;
    private final UUID productId;
    private final String imageUrl;
    private final boolean isPrimary;
    private final Integer displayOrder;
    private final String altText;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ProductImageResponseDTO(UUID id, UUID productId, String imageUrl, boolean isPrimary,
            Integer displayOrder, String altText, LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.productId = productId;
        this.imageUrl = imageUrl;
        this.isPrimary = isPrimary;
        this.displayOrder = displayOrder;
        this.altText = altText;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}