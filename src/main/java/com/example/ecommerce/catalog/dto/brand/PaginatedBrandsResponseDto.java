package com.example.ecommerce.catalog.dto.brand;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaginatedBrandsResponseDto(UUID id, String name, String description, String logoUrl,
        LocalDateTime createdAt, LocalDateTime updatedAt) {
}
