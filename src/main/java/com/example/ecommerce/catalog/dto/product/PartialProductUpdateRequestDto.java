package com.example.ecommerce.catalog.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public record PartialProductUpdateRequestDto(
        String productName,

        @Size(max = 20000, message = "Description too long")
        String description,

        @DecimalMin(value = "0.00", inclusive = false, message = "Price must be greater than zero")
        @Digits(integer = 10, fraction = 2)
        BigDecimal price,

        @DecimalMin(value = "0.00", inclusive = false, message = "Weight must be greater than zero")
        @Digits(integer = 10, fraction = 2)
        BigDecimal weight,

        @Min(0)
        Long stockQuantity,

        UUID brandId,
        UUID categoryId,
        String size,
        String color,
        String sku
) {
}
