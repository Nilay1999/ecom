package com.example.ecommerce.catalog.dto.brand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateBrandRequestDto(
                @NotBlank(message = "Brand name cannot be blank") @Size(max = 50, message = "Brand name cannot exceed 50 characters") String name,

                @NotBlank(message = "Brand description cannot be blank") @Size(max = 400, message = "Brand description cannot exceed 400 characters") String description,

                @NotBlank(message = "Logo URL cannot be blank") String logoUrl,

                @NotNull(message = "Active must not be null") Boolean active) {
}
