package com.example.ecommerce.catalog.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.UUID;

public record UpdateProductCategoryDto(
        @NotBlank
        @NotEmpty
        UUID categoryId
) {
}
