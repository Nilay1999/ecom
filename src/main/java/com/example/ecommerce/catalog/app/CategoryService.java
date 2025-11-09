package com.example.ecommerce.catalog.app;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.ecommerce.catalog.domain.Category;
import com.example.ecommerce.catalog.dto.category.CategoryResponseDto;
import com.example.ecommerce.catalog.dto.category.CategoryTreeDto;
import com.example.ecommerce.catalog.dto.category.CreateCategoryRequest;
import com.example.ecommerce.catalog.dto.category.PageResponseDto;
import com.example.ecommerce.catalog.infra.CategoryRepository;
import com.example.ecommerce.common.exception.category.CategoryNotFoundException;

@Service
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepo;

    public CategoryService(CategoryRepository categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    public Category create(String name, String description, UUID parentCategoryId) {
        if (parentCategoryId != null) {
            Category parentCategory = categoryRepo.findById(parentCategoryId)
                    .orElseThrow(() -> new CategoryNotFoundException("Parent Category not found"));
            return saveCategory(name, description, parentCategory);
        }
        return saveCategory(name, description, null);
    }

    public Category findById(UUID id) {
        return categoryRepo.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
    }

    public PageResponseDto<CategoryTreeDto> getPaginatedCategoryTree(int page, int size) {
        Page<Category> categoryPage = categoryRepo
                .findCategoriesWithChildren(PageRequest.of(page, Math.min(size, 100)));

        List<CategoryTreeDto> dtoList = categoryPage.getContent().stream()
                .map(this::categoryTreeResponse)
                .toList();

        return new PageResponseDto<>(
                dtoList,
                categoryPage.getNumber(),
                categoryPage.getSize(),
                categoryPage.getTotalElements(),
                categoryPage.getTotalPages(),
                categoryPage.isLast());
    }

    public PageResponseDto<CategoryResponseDto> getPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        Page<Category> categoryPage = categoryRepo.findAll(pageable);

        List<CategoryResponseDto> dtoList = categoryPage.getContent().stream().map(this::toDto).toList();

        return new PageResponseDto<>(
                dtoList,
                categoryPage.getNumber(),
                categoryPage.getSize(),
                categoryPage.getTotalElements(),
                categoryPage.getTotalPages(),
                categoryPage.isLast());
    }

    public Category findBySlug(String slug) {
        return categoryRepo.findBySlug(slug)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with slug: " + slug));
    }

    public List<CategoryTreeDto> getCategoryTreeByParentId(UUID parentId) {
        Category parent = categoryRepo.findById(parentId)
                .orElseThrow(() -> new CategoryNotFoundException("Parent Category not found with id: " + parentId));

        List<Category> categories = categoryRepo.findByParent(parent);
        return categories.stream().map(this::categoryTreeResponse).toList();
    }

    public Category upsertCategory(UUID id, CreateCategoryRequest request) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
        if (request.getCategoryName() != null) {
            category.updateName(request.getCategoryName());
        }
        if (request.getDescription() != null) {
            category.updateDescription(request.getDescription());
        }
        if (request.getParentCategoryId() != null) {
            Category parent = categoryRepo.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new RuntimeException("Parent not found"));
            category.changeParent(parent);
        } else {
            category.changeParent(null);
        }
        return categoryRepo.save(category);
    }

    // -------------------- private helpers --------------------

    private Category saveCategory(String name, String description, Category parent) {
        Category.Builder builder = new Category.Builder().setName(name).setDescription(description);

        if (parent != null)
            builder.setParent(parent);

        return categoryRepo.save(builder.build());
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

    private CategoryTreeDto categoryTreeResponse(Category category) {
        CategoryTreeDto dto = new CategoryTreeDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setSlug(category.getSlug());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        dto.setRootCategory(category.isRootCategory());

        // Recursively convert subcategories
        // List<CategoryTreeDto> subCategoryDtos = category.getSubCategories().stream()
        // .map(this::categoryTreeResponse)
        // .toList();
        // dto.setSubCategories(subCategoryDtos);
        if (category.getSubCategories() != null && !category.getSubCategories().isEmpty()) {
            dto.setSubCategories(
                    category.getSubCategories().stream()
                            .map(this::categoryTreeResponse)
                            .collect(Collectors.toList()));
        }

        return dto;
    }
}
