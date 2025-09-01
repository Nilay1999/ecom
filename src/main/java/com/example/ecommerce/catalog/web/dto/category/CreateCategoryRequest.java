package com.example.ecommerce.catalog.web.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateCategoryRequest {
    @NotBlank
    private String categoryName;

    @NotBlank
    private String description;

    private UUID parentCategoryId;
}
