package com.example.ecommerce.catalog.web.controller;

import com.example.ecommerce.catalog.app.CategoryService;
import com.example.ecommerce.catalog.domain.Category;
import com.example.ecommerce.catalog.web.dto.category.CategoryPageResponseDto;
import com.example.ecommerce.catalog.web.dto.category.CategoryResponseDto;
import com.example.ecommerce.catalog.web.dto.category.CreateCategoryRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/category")
@Tag(name = "Categories", description = "Category management APIs")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<Category> create(@RequestBody CreateCategoryRequest request) {
        Category category;
        if (request.getParentCategoryId() != null) {
            category = categoryService.create(request.getCategoryName(), request.getDescription(),
                                              request.getParentCategoryId());

        } else {
            category = categoryService.create(request.getCategoryName(), request.getDescription());
        }
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "Get paginated list of categories", description = "Returns a page of categories with " +
            "configurable page number and size.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved categories", content =
    @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryPageResponseDto.class)))
    @GetMapping("/")
    public ResponseEntity<CategoryPageResponseDto> getCategories(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0", name =
                    "page") int page,
            @Parameter(description = "Page size (max 100)", example = "5") @RequestParam(defaultValue = "5", name =
                    "size") int size) {
        return ResponseEntity.ok(categoryService.getPaginated(page, Math.min(size, 100)));
    }

    @Operation(summary = "Get paginated list of categories with Subcategories", description = "Returns a page of " +
            "categories with " + "configurable page number and size.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved categories", content =
    @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryPageResponseDto.class)))
    @GetMapping("/tree")
    public ResponseEntity<Page<Category>> getCategoriesTree(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0", name =
                    "page") int page,
            @Parameter(description = "Page size (max 100)", example = "5") @RequestParam(defaultValue = "5", name =
                    "size") int size) {
        return ResponseEntity.ok(categoryService.getPaginatedCategoryTree(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<Category> getCategoryBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(categoryService.findBySlug(slug));
    }

    private CategoryResponseDto toDto(Category category) {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setSlug(category.getSlug());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        dto.setRootCategory(category.isRootCategory());
        return dto;
    }
}
