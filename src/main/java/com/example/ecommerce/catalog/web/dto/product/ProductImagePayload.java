package com.example.ecommerce.catalog.web.dto.product;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ProductImagePayload {
    @NotNull
    private String imageUrl;

    @NotNull
    private boolean isPrimary;
}
