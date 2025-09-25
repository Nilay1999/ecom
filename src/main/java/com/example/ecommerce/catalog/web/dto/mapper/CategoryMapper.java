package com.example.ecommerce.catalog.web.dto.mapper;

import com.example.ecommerce.catalog.domain.Category;
import com.example.ecommerce.catalog.web.dto.category.CategoryResponseDTO;
import com.example.ecommerce.catalog.web.dto.category.CreateCategoryRequest;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryMapper {

    public static Category toEntity(CreateCategoryRequest dto, Category parentCategory) {
        if (dto == null) {
            return null;
        }

        String slug = dto.getSlug();
        if (slug == null || slug.trim().isEmpty()) {
            slug = Category.generateSlug(dto.getCategoryName());
        }

        return new Category.Builder()
                .setName(dto.getCategoryName())
                .setDescription(dto.getDescription())
                .setParent(parentCategory)
                .setSlug(slug)
                .build();
    }

    public static CategoryResponseDTO toResponseDTO(Category entity) {
        if (entity == null) {
            return null;
        }

        CategoryResponseDTO.CategorySummary parentSummary = null;
        if (entity.getParent() != null) {
            parentSummary = new CategoryResponseDTO.CategorySummary(
                    entity.getParent().getId(),
                    entity.getParent().getName(),
                    entity.getParent().getSlug(),
                    entity.getParent().getDepth());
        }

        List<CategoryResponseDTO.CategorySummary> childSummaries = entity.getSubCategories().stream()
                .map(child -> new CategoryResponseDTO.CategorySummary(
                        child.getId(),
                        child.getName(),
                        child.getSlug(),
                        child.getDepth()))
                .collect(Collectors.toList());

        return new CategoryResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getSlug(),
                parentSummary,
                childSummaries,
                entity.getProducts().size(), // Product count
                entity.getDepth(),
                entity.hasSubCategories(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static List<CategoryResponseDTO> toResponseDTOList(List<Category> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(CategoryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public static void updateEntityFromDTO(Category entity, CreateCategoryRequest dto, Category parentCategory) {
        if (entity == null || dto == null) {
            return;
        }

        if (dto.getCategoryName() != null) {
            entity.updateName(dto.getCategoryName());
        }

        if (dto.getDescription() != null) {
            entity.updateDescription(dto.getDescription());
        }

        if (dto.getSlug() != null) {
            entity.updateSlug(dto.getSlug());
        }

        // Handle parent category change
        if (parentCategory != entity.getParent()) {
            if (entity.getParent() != null) {
                entity.getParent().removeSubCategory(entity);
            }

            entity.updateParent(parentCategory);

            if (parentCategory != null) {
                parentCategory.addSubCategory(entity);
            }
        }
    }

    public static CategoryResponseDTO.CategorySummary toCategorySummary(Category entity) {
        if (entity == null) {
            return null;
        }

        return new CategoryResponseDTO.CategorySummary(
                entity.getId(),
                entity.getName(),
                entity.getSlug(),
                entity.getDepth());
    }
}