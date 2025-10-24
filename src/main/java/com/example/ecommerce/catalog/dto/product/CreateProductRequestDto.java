package com.example.ecommerce.catalog.dto.product;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;
import com.example.ecommerce.catalog.domain.Product;

public record CreateProductRequestDto(
        @NotBlank String productName,
        String description,
        @NotNull UUID brandId,
        @NotNull UUID categoryId,
        @DecimalMin(value = "0.00") @Digits(integer = 1, fraction = 2) BigDecimal rating,
        @Min(0) long stockQuantity,
        @DecimalMin(value = "0.00", inclusive = false) @Digits(integer = 10, fraction =
                2) BigDecimal weight,
        @DecimalMin(value = "0.00", inclusive = false) @Digits(integer = 10, fraction =
                2) BigDecimal price,
        String size,
        String color,
        String sku,
        @NotNull Product.Status status) {
}
