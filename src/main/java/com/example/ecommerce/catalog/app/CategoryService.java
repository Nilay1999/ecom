package com.example.ecommerce.catalog.app;

import com.example.ecommerce.catalog.domain.Category;
import com.example.ecommerce.catalog.infra.CategoryRepository;
import com.example.ecommerce.catalog.web.dto.category.CategoryResponseDto;
import com.example.ecommerce.catalog.web.dto.category.CreateCategoryRequest;
import com.example.ecommerce.catalog.web.dto.category.PageResponseDto;
import com.example.ecommerce.common.exception.category.CategoryNotFoundException;
import com.example.ecommerce.common.exception.category.DuplicateCategoryException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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

    public Page<Category> getPaginatedCategoryTree(int page, int size) {
        return categoryRepo.findCategoriesWithChildren(PageRequest.of(page, Math.min(size, 100)));
    }

    public PageResponseDto<CategoryResponseDto> getPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        Page<Category> categoryPage = categoryRepo.findAll(pageable);

        List<CategoryResponseDto> dtoList = categoryPage.getContent().stream().map(this::toDto).toList();

        PageResponseDto<CategoryResponseDto> response = new PageResponseDto<CategoryResponseDto>();
        response.setContent(dtoList);
        response.setNumber(categoryPage.getNumber());
        response.setSize(categoryPage.getSize());
        response.setTotalElements(categoryPage.getTotalElements());
        response.setTotalPages(categoryPage.getTotalPages());
        response.setHasNext(categoryPage.hasNext());
        response.setHasPrevious(categoryPage.hasPrevious());

        return response;
    }

    public Category findBySlug(String slug) {
        return categoryRepo.findBySlug(slug)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with slug: " + slug));
    }

    public List<Category> getCategoryTreeByParentId(UUID parentId) {
        Category parent = categoryRepo.findById(parentId)
                .orElseThrow(() -> new CategoryNotFoundException("Parent Category not found with id: " + parentId));

        return categoryRepo.findByParent(parent);
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
