package com.example.ecommerce.catalog.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Represents a product category")
public class CategoryResponseDto {
    @Schema(description = "Unique identifier of the category")
    private UUID id;

    @Schema(description = "Name of the category", example = "Electronics")
    private String name;

    @Schema(description = "Description of the category", example = "Devices and gadgets")
    private String description;

    @Schema(description = "URL-friendly slug", example = "electronics")
    private String slug;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    @Schema(description = "True if this is a root category (no parent)")
    private boolean rootCategory;
}
