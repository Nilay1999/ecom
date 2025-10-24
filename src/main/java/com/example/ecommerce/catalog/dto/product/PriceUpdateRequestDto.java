package com.example.ecommerce.catalog.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;

public record PriceUpdateRequestDto(
        @DecimalMin(value = "0.00", inclusive = false, message = "Price must be greater than zero")
        @Digits(integer = 10, fraction = 2)
        BigDecimal price
) {
}
