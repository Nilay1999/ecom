package com.example.ecommerce.catalog.dto.brand;

import jakarta.validation.constraints.Max;

public record UpdateBrandRequestDto(@Max(50) String name, @Max(400) String description, String logoUrl,
                                    Boolean active) {
}
