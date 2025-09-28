package com.example.ecommerce.catalog.web.dto.product;

import com.example.ecommerce.catalog.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateProductResponseDto(UUID id, String productName, BigDecimal price, long stockQuantity,
                                       Product.Status status) {
}
