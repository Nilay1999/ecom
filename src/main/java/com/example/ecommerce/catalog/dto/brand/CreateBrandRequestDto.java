package com.example.ecommerce.catalog.dto.brand;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;


public record CreateBrandRequestDto(@NotBlank(message = "Brand name cannot be blank") @Max(50) String name,
                                    @NotBlank(message = "Brand description cannot be blank") @Max(400) String description,
                                    @NotBlank String logoUrl, @NotBlank() Boolean active) {
}
