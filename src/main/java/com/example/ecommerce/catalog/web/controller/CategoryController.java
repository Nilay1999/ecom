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

    @GetMapping
    public ResponseEntity<Page<Category>> getPaginatedCategories(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Category> categories = categoryService.getPaginated(page, size);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<Category> getCategoryBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(categoryService.findBySlug(slug));
    }
}
