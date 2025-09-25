package com.example.ecommerce.catalog.web.dto.category;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class CategoryResponseDTO {
    private final UUID id;
    private final String name;
    private final String description;
    private final String slug;
    private final CategorySummary parentCategory;
    private final List<CategorySummary> childCategories;
    private final int productCount;
    private final int level;
    private final boolean hasChildren;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public record CategorySummary(UUID id, String name, String slug, int level) {
    }

    public CategoryResponseDTO(UUID id, String name, String description, String slug,
            CategorySummary parentCategory, List<CategorySummary> childCategories,
            int productCount, int level, boolean hasChildren,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.slug = slug;
        this.parentCategory = parentCategory;
        this.childCategories = childCategories;
        this.productCount = productCount;
        this.level = level;
        this.hasChildren = hasChildren;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}