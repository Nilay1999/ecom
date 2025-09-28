package com.example.ecommerce.catalog.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@Entity
@Table(name = "products", indexes = {@Index(name = "idx_product_name", columnList = "productName"), @Index(name =
        "idx_brand_name", columnList = "brandName")})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    private String productName;

    @Column(length = 20000)
    private String description;

    @Digits(integer = 1, fraction = 2)
    @Column(precision = 3, scale = 2)
    private BigDecimal rating = BigDecimal.ONE;

    @Min(0)
    @Column(nullable = false)
    private long stockQuantity = 0;

    @DecimalMin(value = "0.00", inclusive = false)
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal weight;

    @DecimalMin(value = "0.00", inclusive = false)
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnore
    private Category category;

    @Column(length = 100)
    private String size;

    @Column(length = 50)
    private String color;

    @Column(length = 50)
    private String sku;
    @Column(name = "status", length = 25, nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.OUT_OF_STOCK;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> productImages = new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected Product() {
    }

    private Product(Builder builder) {
        this.productName = builder.productName;
        this.description = builder.description;
        this.rating = builder.rating != null ? builder.rating : BigDecimal.ZERO;
        this.stockQuantity = builder.stockQuantity;
        this.price = builder.price;
        this.weight = builder.weight;
        this.category = builder.category;
        this.status = builder.status;
        this.brand = builder.brand;
        this.color = builder.color;
        this.sku = builder.sku;
        this.size = builder.size;
    }

    public void addImage(ProductImage image) {
        productImages.add(image);
        image.setProduct(this);
    }

    public void removeImage(ProductImage image) {
        productImages.remove(image);
        image.setProduct(null);
    }

    public Optional<ProductImage> getPrimaryImage() {
        return productImages.stream().filter(ProductImage::isPrimary).findFirst();
    }

    public void updateStock(long quantity) {
        this.stockQuantity = quantity;
        updateStatusBasedOnStock();
    }

    public void assignToBrand(Brand brand) {
        this.brand = brand;
    }

    public void updatePrice(BigDecimal newPrice) {
        if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        this.price = newPrice;
    }

    public void updateStatus(Status status) {
        this.status = status;
    }

    private void updateStatusBasedOnStock() {
        if (this.stockQuantity > 0) {
            this.status = Status.ACTIVE;
        } else {
            this.status = Status.OUT_OF_STOCK;
        }
    }

    public void assignToCategory(Category category) {
        if (this.category != null) {
            this.category.getProducts().remove(this);
        }
        this.category = category;
        if (category != null) {
            category.getProducts().add(this);
        }
    }

    // --- JPA Lifecycle Hooks ---
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Status {
        ACTIVE, IN_ACTIVE, OUT_OF_STOCK
    }

    // --- Builder ---
    public static class Builder {
        private String productName;
        private String description;
        private BigDecimal rating;
        private long stockQuantity;
        private BigDecimal weight;
        private BigDecimal price;
        private Category category;
        private Status status;
        private Brand brand;
        private String size;
        private String color;
        private String sku;

        public Builder setProductName(String productName) {
            this.productName = productName;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setRating(BigDecimal rating) {
            this.rating = rating;
            return this;
        }

        public Builder setStockQuantity(long stockQuantity) {
            this.stockQuantity = stockQuantity;
            return this;
        }

        public Builder setWeight(BigDecimal weight) {
            this.weight = weight;
            return this;
        }

        public Builder setPrice(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder setCategory(Category category) {
            this.category = category;
            return this;
        }

        public Builder setStatus(Status status) {
            this.status = status;
            return this;
        }

        public Builder setBrand(Brand brand) {
            this.brand = brand;
            return this;
        }

        public Builder setSize(String size) {
            this.size = size;
            return this;
        }

        public Builder setColor(String color) {
            this.color = color;
            return this;
        }

        public Builder setSku(String sku) {
            this.sku = sku;
            return this;
        }

        private void validate() {
            if (productName == null || productName.trim().isEmpty()) {
                throw new IllegalStateException("Product name is required");
            }
            if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalStateException("Price must be greater than zero");
            }
            if (category == null) {
                throw new IllegalStateException("Category is required");
            }
        }

        public Product build() {
            return new Product(this);
        }
    }
}
