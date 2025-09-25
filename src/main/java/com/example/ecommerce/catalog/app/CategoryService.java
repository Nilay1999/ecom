package com.example.ecommerce.catalog.app;

import com.example.ecommerce.catalog.domain.Category;
import com.example.ecommerce.catalog.infra.CategoryRepository;
import com.example.ecommerce.catalog.util.SlugUtil;
import com.example.ecommerce.catalog.web.exception.category.CategoryNotFoundException;
import com.example.ecommerce.catalog.web.exception.category.DuplicateCategoryException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public Category update(UUID id, String name, String description) {
        Category category = findById(id);

        // Validate and update name if changed
        if (name != null && !name.equals(category.getName())) {
            if (categoryRepo.existsByNameAndIdNot(name, id)) {
                throw new DuplicateCategoryException("A category with name '" + name + "' already exists");
            }
            category.updateName(name);

            // Generate new slug if name changed
            String newSlug = generateUniqueSlugForUpdate(name, id);
            category.updateSlug(newSlug);
        }

        // Update description
        if (description != null) {
            category.updateDescription(description);
        }

        return categoryRepo.save(category);
    }

    public Category updateParent(UUID categoryId, UUID parentId) {
        Category category = findById(categoryId);
        Category parent = parentId != null ? findById(parentId) : null;

        category.updateParent(parent);
        return categoryRepo.save(category);
    }

    public void delete(UUID id) {
        Category category = findById(id);

        // Check if category has subcategories
        if (category.hasSubCategories()) {
            throw new IllegalStateException(
                    "Cannot delete category with subcategories. Delete or move subcategories first.");
        }

        // Check if category has products
        if (category.hasProducts()) {
            throw new IllegalStateException("Cannot delete category with products. Move or delete products first.");
        }

        categoryRepo.delete(category);
    }

    // -------------------- Category Hierarchy Management --------------------

    public List<Category> getRootCategories() {
        return categoryRepo.findRootCategories();
    }

    public List<Category> getSubCategories(UUID parentId) {
        return categoryRepo.findByParentId(parentId);
    }

    public List<Category> getAllDescendants(UUID categoryId) {
        return categoryRepo.findAllDescendants(categoryId);
    }

    public List<Category> getCategoryPath(UUID categoryId) {
        return categoryRepo.findCategoryPath(categoryId);
    }

    public List<Category> getCategoriesByDepth(int depth) {
        return categoryRepo.findByDepthLevel(depth);
    }

    public long getSubCategoryCount(UUID parentId) {
        return categoryRepo.countSubcategoriesByParentId(parentId);
    }

    public long getProductCount(UUID categoryId) {
        return categoryRepo.countProductsByCategoryId(categoryId);
    }

    // -------------------- Category Tree Operations --------------------

    public List<Category> getCategoriesWithSubcategories() {
        return categoryRepo.findCategoriesWithSubcategories();
    }

    public List<Category> getCategoriesWithProducts() {
        return categoryRepo.findCategoriesWithProducts();
    }

    public List<Category> getEmptyLeafCategories() {
        return categoryRepo.findEmptyLeafCategories();
    }

    public List<Category> getCategoriesOrderedByProductCount() {
        return categoryRepo.findCategoriesOrderByProductCountDesc();
    }

    public List<Category> getCategoriesWithMinProductCount(long minCount) {
        return categoryRepo.findCategoriesWithMinProductCount(minCount);
    }

    // -------------------- Search and Filtering --------------------

    public List<Category> searchCategories(String searchTerm) {
        return categoryRepo.searchByNameOrDescription(searchTerm);
    }

    public List<Category> findByNameContaining(String name) {
        return categoryRepo.findByNameContainingIgnoreCase(name);
    }

    public Page<Category> getPaginatedWithSort(int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return categoryRepo.findAll(pageable);
    }

    // -------------------- Slug Management --------------------

    public String generateSlugFromName(String name) {
        return generateUniqueSlug(name);
    }

    public boolean isSlugAvailable(String slug) {
        return !categoryRepo.existsBySlug(slug);
    }

    public boolean isSlugAvailableForUpdate(String slug, UUID categoryId) {
        return !categoryRepo.existsBySlugAndIdNot(slug, categoryId);
    }

    // -------------------- Validation Methods --------------------

    public boolean canDeleteCategory(UUID categoryId) {
        Category category = findById(categoryId);
        return !category.hasSubCategories() && !category.hasProducts();
    }

    public boolean canMoveCategory(UUID categoryId, UUID newParentId) {
        Category category = findById(categoryId);

        if (newParentId == null) {
            return true; // Can always move to root
        }

        Category newParent = findById(newParentId);

        // Cannot move to self
        if (category.equals(newParent)) {
            return false;
        }

        // Cannot move to descendant (would create circular reference)
        return !category.isAncestorOf(newParent);
    }

    // -------------------- private helpers --------------------

    private Category saveCategory(String name, String description, Category parent) {
        String slug = generateUniqueSlug(name);
        validateCategoryData(name, slug);

        Category.Builder builder = new Category.Builder().setName(name)
                .setDescription(description)
                .setSlug(slug);

        if (parent != null)
            builder.setParent(parent);

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

    private String generateUniqueSlug(String name) {
        String baseSlug = SlugUtil.toSlug(name);
        String slug = baseSlug;
        int counter = 1;

        while (categoryRepo.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }
        return slug;
    }

    private String generateUniqueSlugForUpdate(String name, UUID categoryId) {
        String baseSlug = SlugUtil.toSlug(name);
        String slug = baseSlug;
        int counter = 1;

        while (categoryRepo.existsBySlugAndIdNot(slug, categoryId)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }
        return slug;
    }
}
