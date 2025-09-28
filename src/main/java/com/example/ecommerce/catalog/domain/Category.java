package com.example.ecommerce.catalog.domain;

import com.example.ecommerce.common.util.SlugGenerator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Table(name = "categories")
public class Category {
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Category> subCategories = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.PERSIST)
    private final List<Product> products = new ArrayList<>();

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

    @Column(length = 200, unique = true, nullable = false)
    private String slug;

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private Category(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.parent = builder.parent;
        this.slug = builder.slug;
    }

    protected Category() {
    }

    public void updateName(String name) {
        this.name = name;
        this.slug = generateSlug(name);
    }

    private String generateSlug(String name) {
        return SlugGenerator.generateSlug(name);
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void changeParent(Category newParent) {
        if (this.parent != null) {
            this.parent.getSubCategories().remove(this);
        }
        this.parent = newParent;
        if (newParent != null) {
            newParent.getSubCategories().add(this);
        }
    }

    public boolean isRootCategory() {
        return this.parent == null;
    }

    public boolean hasSubCategories() {
        return !this.subCategories.isEmpty();
    }

    public boolean hasProducts() {
        return !this.products.isEmpty();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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
}
