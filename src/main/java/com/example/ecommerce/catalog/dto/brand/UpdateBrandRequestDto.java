package com.example.ecommerce.catalog.dto.brand;

import jakarta.validation.constraints.Size;

public record UpdateBrandRequestDto(@Size(max = 50) String name, @Size(max = 400) String description, String logoUrl,
        Boolean active) {
}
