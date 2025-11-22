package com.example.ecommerce.catalog.dto.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaginatedProductListResponseDto(
        UUID id,
        String product,
        String description,
        BigDecimal rating,
        BigDecimal weight,
        BigDecimal price,
        String size,
        String sku,
        String brandname,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {}
