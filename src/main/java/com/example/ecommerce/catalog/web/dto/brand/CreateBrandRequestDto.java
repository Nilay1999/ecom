package com.example.ecommerce.catalog.web.dto.brand;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateBrandRequestDto {
    @NotBlank(message = "Brand name cannot be blank")
    @Max(50)
    private String name;

    @NotBlank(message = "Brand description cannot be blank")
    @Max(400)
    private String description;

    @NotBlank
    private String logoUrl;

    @NotBlank()
    private Boolean active;
}
