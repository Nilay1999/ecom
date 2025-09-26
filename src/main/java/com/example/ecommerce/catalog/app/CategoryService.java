package com.example.ecommerce.catalog.app;

import com.example.ecommerce.catalog.domain.Category;
import com.example.ecommerce.catalog.infra.CategoryRepository;
import com.example.ecommerce.common.exception.category.CategoryNotFoundException;
import com.example.ecommerce.common.exception.category.DuplicateCategoryException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepo;

    public CategoryService(CategoryRepository categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    public Category create(String name, String description) {
        return saveCategory(name, description, null);
    }

    public Category create(String name, String description, UUID parentCategoryId) {
        Category parentCategory = categoryRepo.findById(parentCategoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Parent Category not found"));
        return saveCategory(name, description, parentCategory);
    }

    public Category findById(UUID id) {
        return categoryRepo.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
    }

    public Page<Category> getPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return categoryRepo.findAll(pageable);
    }

    public Category findBySlug(String slug) {
        return categoryRepo.findBySlug(slug)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with slug: " + slug));
    }

    // -------------------- private helpers --------------------

    private Category saveCategory(String name, String description, Category parent) {
        Category.Builder builder = new Category.Builder().setName(name).setDescription(description);

        if (parent != null) builder.setParent(parent);

        return categoryRepo.save(builder.build());
    }

    private void validateCategoryData(String name, String slug) {
        if (categoryRepo.existsByName(name)) {
            throw new DuplicateCategoryException("A category with name '" + name + "' already exists");
        }
        if (categoryRepo.existsBySlug(slug)) {
            throw new DuplicateCategoryException("A category with slug '" + slug + "' already exists");
        }
    }
}
