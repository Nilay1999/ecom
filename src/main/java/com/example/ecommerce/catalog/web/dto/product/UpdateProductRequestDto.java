package com.example.ecommerce.catalog.web.dto.product;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public record UpdateProductRequestDto(
        @NotBlank(message = "Product name is required")
        String productName,

        @Size(max = 20000, message = "Description too long")
        String description,

        @DecimalMin(value = "0.00", inclusive = false, message = "Price must be greater than zero")
        @Digits(integer = 10, fraction = 2)
        BigDecimal price,

        @DecimalMin(value = "0.00", inclusive = false, message = "Weight must be greater than zero")
        @Digits(integer = 10, fraction = 2)
        BigDecimal weight,

        @NotNull(message = "Stock quantity cannot be null")
        @Min(0)
        Long stockQuantity,

        @NotNull(message = "Brand ID is required")
        UUID brandId,

        @NotNull(message = "Category ID is required")
        UUID categoryId,

        String size,
        String color,
        String sku
) {
}

