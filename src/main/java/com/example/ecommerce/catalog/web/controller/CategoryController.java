package com.example.ecommerce.catalog.web.controller;

import com.example.ecommerce.catalog.app.CategoryService;
import com.example.ecommerce.catalog.domain.Category;
import com.example.ecommerce.catalog.web.dto.category.CreateCategoryRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public Category create(@RequestBody CreateCategoryRequest request) {
        if (request.getParentCategoryId() != null) {
            return categoryService.create(request.getCategoryName(), request.getDescription(),
                                          request.getParentCategoryId());
        }
        return categoryService.create(request.getCategoryName(), request.getDescription());
    }

    @GetMapping
    public ResponseEntity<Page<Category>> getPaginatedCategories(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Category> categories = categoryService.getPaginated(page, size);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable UUID id) {
        return categoryService.findById(id);
    }

    @GetMapping("/slug/{slug}")
    public Category getCategoryBySlug(@PathVariable String slug) {
        return categoryService.findBySlug(slug);
    }
}
