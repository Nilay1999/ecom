package com.example.ecommerce.catalog.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Category> subCategories = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.PERSIST)
    private final List<Product> products = new ArrayList<>();

    @Column(length = 200, unique = true, nullable = false)
    private String slug;

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected Category() {
    }

    private Category(Builder builder) {
        validateName(builder.name);
        validateDescription(builder.description);
        validateSlug(builder.slug);

        this.name = builder.name;
        this.description = builder.description;
        this.parent = builder.parent;
        this.slug = builder.slug;
    }

    // Domain validation methods
    public static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }

        if (name.length() > 100) {
            throw new IllegalArgumentException("Category name cannot exceed 100 characters");
        }

        // Check for valid characters (alphanumeric, spaces, hyphens, underscores)
        if (!name.matches("^[a-zA-Z0-9\\s\\-_&]+$")) {
            throw new IllegalArgumentException(
                    "Category name contains invalid characters. Only alphanumeric characters, spaces, hyphens, underscores, and ampersands are allowed");
        }
    }

    public static void validateDescription(String description) {
        if (description != null && description.length() > 500) {
            throw new IllegalArgumentException("Category description cannot exceed 500 characters");
        }
    }

    public static void validateSlug(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            throw new IllegalArgumentException("Category slug cannot be null or empty");
        }

        if (slug.length() > 200) {
            throw new IllegalArgumentException("Category slug cannot exceed 200 characters");
        }

        // Slug should be URL-friendly (lowercase, hyphens, no spaces)
        if (!slug.matches("^[a-z0-9\\-]+$")) {
            throw new IllegalArgumentException(
                    "Category slug must contain only lowercase letters, numbers, and hyphens");
        }

        // Slug cannot start or end with hyphen
        if (slug.startsWith("-") || slug.endsWith("-")) {
            throw new IllegalArgumentException("Category slug cannot start or end with a hyphen");
        }

        // Slug cannot have consecutive hyphens
        if (slug.contains("--")) {
            throw new IllegalArgumentException("Category slug cannot contain consecutive hyphens");
        }
    }

    // Business logic methods
    public void updateName(String newName) {
        validateName(newName);
        this.name = newName.trim();
    }

    public void updateDescription(String newDescription) {
        validateDescription(newDescription);
        this.description = newDescription != null ? newDescription.trim() : null;
    }

    public void updateSlug(String newSlug) {
        validateSlug(newSlug);
        this.slug = newSlug.trim().toLowerCase();
    }

    public void updateParent(Category newParent) {
        // Prevent circular references
        if (newParent != null && isAncestorOf(newParent)) {
            throw new IllegalArgumentException("Cannot set parent category that would create a circular reference");
        }

        // Prevent setting self as parent
        if (newParent != null && newParent.equals(this)) {
            throw new IllegalArgumentException("Category cannot be its own parent");
        }

        this.parent = newParent;
    }

    public void addSubCategory(Category subCategory) {
        Objects.requireNonNull(subCategory, "Sub-category cannot be null");

        // Prevent circular references
        if (isDescendantOf(subCategory)) {
            throw new IllegalArgumentException("Cannot add sub-category that would create a circular reference");
        }

        subCategory.updateParent(this);
        if (!subCategories.contains(subCategory)) {
            subCategories.add(subCategory);
        }
    }

    public void removeSubCategory(Category subCategory) {
        Objects.requireNonNull(subCategory, "Sub-category cannot be null");

        if (subCategories.contains(subCategory)) {
            subCategory.updateParent(null);
            subCategories.remove(subCategory);
        }
    }

    // Business query methods
    public boolean isRootCategory() {
        return this.parent == null;
    }

    public boolean hasSubCategories() {
        return !subCategories.isEmpty();
    }

    public boolean hasProducts() {
        return !products.isEmpty();
    }

    public int getDepth() {
        int depth = 0;
        Category current = this.parent;
        while (current != null) {
            depth++;
            current = current.getParent();
        }
        return depth;
    }

    public List<Category> getPath() {
        List<Category> path = new ArrayList<>();
        Category current = this;
        while (current != null) {
            path.add(0, current);
            current = current.getParent();
        }
        return path;
    }

    public String getFullPath() {
        return getPath().stream()
                .map(Category::getName)
                .reduce((a, b) -> a + " > " + b)
                .orElse(this.name);
    }

    public boolean isAncestorOf(Category category) {
        if (category == null) {
            return false;
        }

        Category current = category.getParent();
        while (current != null) {
            if (current.equals(this)) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }

    public boolean isDescendantOf(Category category) {
        if (category == null) {
            return false;
        }

        return category.isAncestorOf(this);
    }

    public List<Category> getAllDescendants() {
        List<Category> descendants = new ArrayList<>();
        for (Category subCategory : subCategories) {
            descendants.add(subCategory);
            descendants.addAll(subCategory.getAllDescendants());
        }
        return descendants;
    }

    public long getTotalProductCount() {
        long count = products.size();
        for (Category subCategory : subCategories) {
            count += subCategory.getTotalProductCount();
        }
        return count;
    }

    public static String generateSlug(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Cannot generate slug from null or empty name");
        }

        String slug = name.trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s\\-]", "") // Remove invalid characters
                .replaceAll("\\s+", "-") // Replace spaces with hyphens
                .replaceAll("-+", "-"); // Replace multiple hyphens with single hyphen

        // Remove leading/trailing hyphens
        slug = slug.replaceAll("^-+|-+$", "");

        if (slug.isEmpty()) {
            throw new IllegalArgumentException("Generated slug is empty after processing name: " + name);
        }

        return slug;
    }

    public boolean isValidCategory() {
        try {
            validateName(this.name);
            validateDescription(this.description);
            validateSlug(this.slug);

            // Check for circular references
            if (this.parent != null && isDescendantOf(this.parent)) {
                return false;
            }

            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // --- Builder ---
    public static class Builder {
        private String name;
        private String description;
        private String slug;
        private Category parent;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setParent(Category parent) {
            this.parent = parent;
            return this;
        }

        public Builder setSlug(String slug) {
            this.slug = slug;
            return this;
        }

        public Category build() {
            return new Category(this);
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        // Validate before persisting
        if (!isValidCategory()) {
            throw new IllegalStateException("Cannot persist invalid Category");
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();

        // Validate before updating
        if (!isValidCategory()) {
            throw new IllegalStateException("Cannot update to invalid Category state");
        }
    }
}
