package com.example.ecommerce.catalog.dto.image;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductImageRequestDto(

        @NotNull(message = "Product ID is required")
        java.util.UUID productId,

        @NotBlank(message = "Image URL is required")
        @Size(max = 500, message = "Image URL must not exceed 500 characters")
        String imageUrl,

        Boolean isPrimary,

        Integer displayOrder,

        @Size(max = 255, message = "Alt text must not exceed 255 characters")
        String altText,

        @Size(max = 50, message = "Image type must not exceed 50 characters")
        String imageType,

        @NotNull(message = "File size is required")
        Long fileSize,

        @Size(max = 100, message = "MIME type must not exceed 100 characters")
        String mimeType,

        @NotNull(message = "Width is required")
        Integer width,

        @NotNull(message = "Height is required")
        Integer height
) {
}
