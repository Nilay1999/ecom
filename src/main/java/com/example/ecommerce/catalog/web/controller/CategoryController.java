package com.example.ecommerce.catalog.web.controller;

import com.example.ecommerce.catalog.app.CategoryService;
import com.example.ecommerce.catalog.domain.Category;
import com.example.ecommerce.catalog.web.dto.category.CreateCategoryRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    private final CategoryService svc;

    public CategoryController(CategoryService svc) {
        this.svc = svc;
    }

    @PostMapping
    public Category create(@RequestBody CreateCategoryRequest request) {
        return svc.create(request.getCategoryName(), request.getDescription());
    }
}
