package com.example.ecommerce.catalog.web.dto.product;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ProductImagePayload {
    public ProductImagePayload(String imageUrl, boolean isPrimary) {
        this.imageUrl = imageUrl;
        this.isPrimary = isPrimary;
    }

    public ProductImagePayload () {}

    @NotNull
    private String imageUrl;

    @NotNull
    private boolean isPrimary;
}
