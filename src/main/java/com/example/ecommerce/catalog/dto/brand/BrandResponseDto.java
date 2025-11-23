package com.example.ecommerce.catalog.dto.brand;

import java.time.LocalDateTime;
import java.util.UUID;

public record BrandResponseDto(
        UUID id,
        String name,
        String description,
        String logoUrl,
        String slug,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
