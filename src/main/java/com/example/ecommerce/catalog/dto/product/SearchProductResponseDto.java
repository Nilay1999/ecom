package com.example.ecommerce.catalog.dto.product;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import com.example.ecommerce.catalog.domain.Product.Status;
import com.example.ecommerce.catalog.domain.ProductImage;

public record SearchProductResponseDto(UUID id, String productName, String description, BigDecimal price,
                                       long stockQuantity, Status status, Optional<ProductImage> primaryImage) {
}
