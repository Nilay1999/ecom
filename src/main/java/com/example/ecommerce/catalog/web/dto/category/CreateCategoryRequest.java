package com.example.ecommerce.catalog.web.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateCategoryRequest {
    @NotBlank
    private String categoryName;

    @NotBlank
    private String description;
}
