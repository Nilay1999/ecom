package com.example.ecommerce.catalog.dto.product;

import java.math.BigDecimal;
import java.util.UUID;
import com.example.ecommerce.catalog.domain.Product;

public record CreateProductResponseDto(
        UUID id,
        String productName,
        BigDecimal price,
        long stockQuantity,
        Product.Status status) {
}
