package com.example.ecommerce.catalog.web.dto.product;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ProductImageRequestDTO {

    @NotNull(message = "Product ID cannot be null")
    private UUID productId;

    @NotBlank(message = "Image URL cannot be blank")
    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    @Pattern(regexp = "^https?://.*\\.(jpg|jpeg|png|gif|webp|svg)(\\?.*)?$", message = "Invalid image URL format. Supported formats: jpg, jpeg, png, gif, webp, svg")
    private String imageUrl;

    @NotNull(message = "Primary flag cannot be null")
    private Boolean isPrimary = false;

    @Min(value = 0, message = "Display order cannot be negative")
    private Integer displayOrder = 0;

    @Size(max = 255, message = "Alt text cannot exceed 255 characters")
    private String altText;
}