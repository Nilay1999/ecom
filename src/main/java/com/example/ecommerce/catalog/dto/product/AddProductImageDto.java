package com.example.ecommerce.catalog.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddProductImageDto(
        @NotBlank(message = "Image URL is required")
        @Pattern(regexp = "^https?://.*", message = "Must be a valid URL")
        String imageUrl,

        Boolean isPrimary,

        @Min(value = 0, message = "Display order must be non-negative")
        Integer displayOrder,

        @Size(max = 255, message = "Alt text too long")
        String altText,

        @Min(value = 0, message = "File size must be non-negative")
        Long fileSize,

        @Pattern(regexp = "^image/(jpeg|png|gif|webp|svg\\+xml)$", message = "Invalid MIME type")
        String mimeType,

        @Min(value = 1, message = "Width must be positive")
        Integer width,

        @Min(value = 1, message = "Height must be positive")
        Integer height
) {
}

