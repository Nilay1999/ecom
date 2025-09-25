package com.example.ecommerce.catalog.web.controller;

import com.example.ecommerce.catalog.app.CategoryService;
import com.example.ecommerce.catalog.domain.Category;
import com.example.ecommerce.catalog.web.dto.category.CategoryResponseDTO;
import com.example.ecommerce.catalog.web.dto.category.CreateCategoryRequest;
import com.example.ecommerce.catalog.web.dto.mapper.CategoryMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // -------------------- CRUD Operations --------------------

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        Category category;
        if (request.getParentCategoryId() != null) {
            category = categoryService.create(request.getCategoryName(), request.getDescription(),
                    request.getParentCategoryId());
        } else {
            category = categoryService.create(request.getCategoryName(), request.getDescription());
        }

        CategoryResponseDTO response = CategoryMapper.toResponseDTO(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable UUID id) {
        Category category = categoryService.findById(id);
        CategoryResponseDTO response = CategoryMapper.toResponseDTO(category);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody CreateCategoryRequest request) {

        Category category = categoryService.update(id, request.getCategoryName(), request.getDescription());
        CategoryResponseDTO response = CategoryMapper.toResponseDTO(category);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------- Category Listing and Pagination --------------------

    @GetMapping
    public ResponseEntity<Page<Category>> getPaginatedCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Page<Category> categories = categoryService.getPaginatedWithSort(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<CategoryResponseDTO> getCategoryBySlug(@PathVariable String slug) {
        Category category = categoryService.findBySlug(slug);
        CategoryResponseDTO response = CategoryMapper.toResponseDTO(category);
        return ResponseEntity.ok(response);
    }

    // -------------------- Category Hierarchy Management --------------------

    @GetMapping("/root")
    public ResponseEntity<List<CategoryResponseDTO>> getRootCategories() {
        List<Category> categories = categoryService.getRootCategories();
        List<CategoryResponseDTO> response = CategoryMapper.toResponseDTOList(categories);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{parentId}/subcategories")
    public ResponseEntity<List<CategoryResponseDTO>> getSubCategories(@PathVariable UUID parentId) {
        List<Category> categories = categoryService.getSubCategories(parentId);
        List<CategoryResponseDTO> response = CategoryMapper.toResponseDTOList(categories);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{categoryId}/descendants")
    public ResponseEntity<List<CategoryResponseDTO>> getAllDescendants(@PathVariable UUID categoryId) {
        List<Category> categories = categoryService.getAllDescendants(categoryId);
        List<CategoryResponseDTO> response = CategoryMapper.toResponseDTOList(categories);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{categoryId}/path")
    public ResponseEntity<List<CategoryResponseDTO>> getCategoryPath(@PathVariable UUID categoryId) {
        List<Category> categories = categoryService.getCategoryPath(categoryId);
        List<CategoryResponseDTO> response = CategoryMapper.toResponseDTOList(categories);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/depth/{depth}")
    public ResponseEntity<List<CategoryResponseDTO>> getCategoriesByDepth(@PathVariable int depth) {
        List<Category> categories = categoryService.getCategoriesByDepth(depth);
        List<CategoryResponseDTO> response = CategoryMapper.toResponseDTOList(categories);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{categoryId}/parent/{parentId}")
    public ResponseEntity<CategoryResponseDTO> updateParent(
            @PathVariable UUID categoryId,
            @PathVariable UUID parentId) {

        Category category = categoryService.updateParent(categoryId, parentId);
        CategoryResponseDTO response = CategoryMapper.toResponseDTO(category);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{categoryId}/remove-parent")
    public ResponseEntity<CategoryResponseDTO> removeParent(@PathVariable UUID categoryId) {
        Category category = categoryService.updateParent(categoryId, null);
        CategoryResponseDTO response = CategoryMapper.toResponseDTO(category);
        return ResponseEntity.ok(response);
    }

    // -------------------- Category Statistics --------------------

    @GetMapping("/{parentId}/subcategories/count")
    public ResponseEntity<Map<String, Long>> getSubCategoryCount(@PathVariable UUID parentId) {
        long count = categoryService.getSubCategoryCount(parentId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/{categoryId}/products/count")
    public ResponseEntity<Map<String, Long>> getProductCount(@PathVariable UUID categoryId) {
        long count = categoryService.getProductCount(categoryId);
        return ResponseEntity.ok(Map.of("productCount", count));
    }

    // -------------------- Category Tree Operations --------------------

    @GetMapping("/with-subcategories")
    public ResponseEntity<List<CategoryResponseDTO>> getCategoriesWithSubcategories() {
        List<Category> categories = categoryService.getCategoriesWithSubcategories();
        List<CategoryResponseDTO> response = CategoryMapper.toResponseDTOList(categories);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/with-products")
    public ResponseEntity<List<CategoryResponseDTO>> getCategoriesWithProducts() {
        List<Category> categories = categoryService.getCategoriesWithProducts();
        List<CategoryResponseDTO> response = CategoryMapper.toResponseDTOList(categories);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/empty-leaf")
    public ResponseEntity<List<CategoryResponseDTO>> getEmptyLeafCategories() {
        List<Category> categories = categoryService.getEmptyLeafCategories();
        List<CategoryResponseDTO> response = CategoryMapper.toResponseDTOList(categories);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ordered-by-product-count")
    public ResponseEntity<List<CategoryResponseDTO>> getCategoriesOrderedByProductCount() {
        List<Category> categories = categoryService.getCategoriesOrderedByProductCount();
        List<CategoryResponseDTO> response = CategoryMapper.toResponseDTOList(categories);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/min-product-count/{minCount}")
    public ResponseEntity<List<CategoryResponseDTO>> getCategoriesWithMinProductCount(@PathVariable long minCount) {
        List<Category> categories = categoryService.getCategoriesWithMinProductCount(minCount);
        List<CategoryResponseDTO> response = CategoryMapper.toResponseDTOList(categories);
        return ResponseEntity.ok(response);
    }

    // -------------------- Search and Filtering --------------------

    @GetMapping("/search")
    public ResponseEntity<List<CategoryResponseDTO>> searchCategories(@RequestParam String searchTerm) {
        List<Category> categories = categoryService.searchCategories(searchTerm);
        List<CategoryResponseDTO> response = CategoryMapper.toResponseDTOList(categories);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/name")
    public ResponseEntity<List<CategoryResponseDTO>> findByNameContaining(@RequestParam String name) {
        List<Category> categories = categoryService.findByNameContaining(name);
        List<CategoryResponseDTO> response = CategoryMapper.toResponseDTOList(categories);
        return ResponseEntity.ok(response);
    }

    // -------------------- Slug Management --------------------

    @GetMapping("/slug/generate")
    public ResponseEntity<Map<String, String>> generateSlugFromName(@RequestParam String name) {
        String slug = categoryService.generateSlugFromName(name);
        return ResponseEntity.ok(Map.of("slug", slug));
    }

    @GetMapping("/slug/{slug}/available")
    public ResponseEntity<Map<String, Boolean>> isSlugAvailable(@PathVariable String slug) {
        boolean available = categoryService.isSlugAvailable(slug);
        return ResponseEntity.ok(Map.of("available", available));
    }

    @GetMapping("/slug/{slug}/available-for-update/{categoryId}")
    public ResponseEntity<Map<String, Boolean>> isSlugAvailableForUpdate(
            @PathVariable String slug,
            @PathVariable UUID categoryId) {

        boolean available = categoryService.isSlugAvailableForUpdate(slug, categoryId);
        return ResponseEntity.ok(Map.of("available", available));
    }

    // -------------------- Validation Methods --------------------

    @GetMapping("/{categoryId}/can-delete")
    public ResponseEntity<Map<String, Boolean>> canDeleteCategory(@PathVariable UUID categoryId) {
        boolean canDelete = categoryService.canDeleteCategory(categoryId);
        return ResponseEntity.ok(Map.of("canDelete", canDelete));
    }

    @GetMapping("/{categoryId}/can-move-to/{newParentId}")
    public ResponseEntity<Map<String, Boolean>> canMoveCategory(
            @PathVariable UUID categoryId,
            @PathVariable UUID newParentId) {

        boolean canMove = categoryService.canMoveCategory(categoryId, newParentId);
        return ResponseEntity.ok(Map.of("canMove", canMove));
    }

    // -------------------- Bulk Operations --------------------
    // Note: These would require additional service methods to be implemented

    @DeleteMapping("/bulk")
    public ResponseEntity<Map<String, String>> bulkDeleteCategories(@RequestBody List<UUID> categoryIds) {
        // This would require a bulk delete method in CategoryService
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of("message", "Bulk delete not yet implemented"));
    }

    @PostMapping("/bulk/move")
    public ResponseEntity<Map<String, String>> bulkMoveCategories(
            @RequestBody Map<String, Object> request) {
        // This would require a bulk move method in CategoryService
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of("message", "Bulk move not yet implemented"));
    }
}