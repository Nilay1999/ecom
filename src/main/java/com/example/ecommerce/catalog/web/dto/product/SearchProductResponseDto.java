package com.example.ecommerce.catalog.web.dto.product;

import com.example.ecommerce.catalog.domain.Product.Status;
import com.example.ecommerce.catalog.domain.ProductImage;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public record SearchProductResponseDto(UUID id, String productName, String description, BigDecimal price,
                                       long stockQuantity, Status status, Optional<ProductImage> primaryImage) {
}
