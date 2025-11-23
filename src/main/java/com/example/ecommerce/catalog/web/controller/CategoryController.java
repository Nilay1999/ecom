package com.example.ecommerce.catalog.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.ecommerce.catalog.app.CategoryService;
import com.example.ecommerce.catalog.domain.Category;
import com.example.ecommerce.catalog.dto.category.CategoryResponseDto;
import com.example.ecommerce.catalog.dto.category.CategoryTreeDto;
import com.example.ecommerce.catalog.dto.category.CreateCategoryRequest;
import com.example.ecommerce.catalog.dto.category.PageResponseDto;
import com.example.ecommerce.catalog.dto.common.ApiResponse;

@RestController
@RequestMapping("/category")
@Tag(name = "Categories", description = "Category management APIs")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Category>> create(@RequestBody CreateCategoryRequest request) {
        Category category = categoryService.create(request.getCategoryName(), request.getDescription(),
                request.getParentCategoryId());
        return ResponseEntity.ok(ApiResponse.created("Category created successfully", category));
    }

    @GetMapping
    @Operation(summary = "Get paginated list of categories", description = "Returns a page of " + "categories with "
            + "configurable page number and size.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved categories", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponseDto.class)))
    public ResponseEntity<ApiResponse<PageResponseDto<CategoryResponseDto>>> getCategories(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0", name = "page") int page,
            @Parameter(description = "Page size (max 100)", example = "5") @RequestParam(defaultValue = "5", name = "size") int size) {
        PageResponseDto<CategoryResponseDto> categories = categoryService.getPaginated(page, Math.min(size, 100));
        return ResponseEntity.ok(ApiResponse.success("Category Fetched successfully", categories));
    }

    @GetMapping("/tree")
    @Operation(summary = "Get paginated list of categories with Subcategories", description = "Returns a page of " +
            "categories with " + "configurable page number and size.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved categories", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryTreeDto.class)))
    public ResponseEntity<ApiResponse<PageResponseDto<CategoryTreeDto>>> getCategoriesTree(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0", name = "page") int page,
            @Parameter(description = "Page size (max 100)", example = "5") @RequestParam(defaultValue = "5", name = "size") int size) {
        PageResponseDto<CategoryTreeDto> categoryTree = categoryService.getPaginatedCategoryTree(page, size);
        return ResponseEntity.ok(ApiResponse.success("Category Tree Fetched successfully", categoryTree));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> getCategoryById(@PathVariable(name = "id") UUID id) {
        Category category = categoryService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Category retrieved successfully", category));
    }

    @GetMapping("/parent/{parentId}/tree")
    @Operation(summary = "Get Category tree by parentId")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved categories", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryTreeDto.class)))
    public ResponseEntity<ApiResponse<List<CategoryTreeDto>>> getCategoryTreeByParent(
            @PathVariable(name = "parentId") UUID parentId) {
        List<CategoryTreeDto> categoryTree = categoryService.getCategoryTreeByParentId(parentId);
        return ResponseEntity.ok(ApiResponse.success("Category tree retrieved successfully", categoryTree));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<Category>> getCategoryBySlug(@PathVariable(name = "slug") String slug) {
        Category category = categoryService.findBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success("Category retrieved successfully", category));
    }

    @PutMapping("/{id}")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved categories", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class)))
    public ResponseEntity<ApiResponse<Category>> upsertCategoryById(@PathVariable(name = "id") UUID id,
            @RequestBody CreateCategoryRequest request) {
        Category category = categoryService.upsertCategory(id, request);
        return ResponseEntity.ok(ApiResponse.accepted("Category upserted successfully", category));
    }
}
