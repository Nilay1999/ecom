package com.example.ecommerce.catalog.web.controller;

import com.example.ecommerce.catalog.app.CategoryService;
import com.example.ecommerce.catalog.domain.Category;
import com.example.ecommerce.catalog.web.dto.category.CategoryResponseDto;
import com.example.ecommerce.catalog.web.dto.category.CreateCategoryRequest;
import com.example.ecommerce.catalog.web.dto.category.PageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/category")
@Tag(name = "Categories", description = "Category management APIs")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<Category> create(@RequestBody CreateCategoryRequest request) {
        Category category = categoryService.create(request.getCategoryName(), request.getDescription(),
                                                   request.getParentCategoryId());
        return ResponseEntity.ok(category);
    }

    @GetMapping
    @Operation(summary = "Get paginated list of categories", description =
            "Returns a page of " + "categories with " + "configurable page number and size.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved categories", content =
    @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponseDto.class)))
    public ResponseEntity<PageResponseDto<CategoryResponseDto>> getCategories(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0", name =
                    "page") int page,
            @Parameter(description = "Page size (max 100)", example = "5") @RequestParam(defaultValue = "5", name =
                    "size") int size) {
        return ResponseEntity.ok(categoryService.getPaginated(page, Math.min(size, 100)));
    }

    @GetMapping("/tree")
    @Operation(summary = "Get paginated list of categories with Subcategories", description = "Returns a page of " +
            "categories with " + "configurable page number and size.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved categories", content =
    @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponseDto.class)))
    public ResponseEntity<Page<Category>> getCategoriesTree(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0", name =
                    "page") int page,
            @Parameter(description = "Page size (max 100)", example = "5") @RequestParam(defaultValue = "5", name =
                    "size") int size) {
        return ResponseEntity.ok(categoryService.getPaginatedCategoryTree(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @GetMapping("/parent/{parentId}/tree")
    @Operation(summary = "Get Category tree by parentId")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved categories", content =
    @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class)))
    public ResponseEntity<List<Category>> getCategoryTreeByParent(@PathVariable(name = "parentId") UUID parentId) {
        return ResponseEntity.ok(categoryService.getCategoryTreeByParentId(parentId));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<Category> getCategoryBySlug(@PathVariable(name = "slug") String slug) {
        return ResponseEntity.ok(categoryService.findBySlug(slug));
    }

    @PutMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved categories", content =
    @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class)))
    public ResponseEntity<Category> upsertCategoryById(@PathVariable(name = "id") UUID id,
            @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.ok(categoryService.upsertCategory(id, request));
    }
}
