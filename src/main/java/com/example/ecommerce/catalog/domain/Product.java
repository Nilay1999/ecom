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

    @NotBlank
    @Column(nullable = false)
    private String brandName;

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

    public enum Status {
        ACTIVE, IN_ACTIVE, OUT_OF_STOCK
    }

    @Column(name = "status", length = 25, nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.OUT_OF_STOCK;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> productImages = new ArrayList<>();

    public void addImage(ProductImage image) {
        productImages.add(image);
        image.setProduct(this);
    }

    public void removeImage(ProductImage image) {
        productImages.remove(image);
        image.setProduct(null);
    }

    public Optional<ProductImage> getPrimaryImage() {
        return productImages.stream()
                .filter(ProductImage::isPrimary)
                .findFirst();
    }

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected Product() {
    }

    private Product(Builder builder) {
        this.productName = builder.productName;
        this.description = builder.description;
        this.brandName = builder.brandName;
        this.rating = builder.rating != null ? builder.rating : BigDecimal.ZERO;
        this.stockQuantity = builder.stockQuantity;
        this.price = builder.price;
        this.weight = builder.weight;
        this.category = builder.category;
        this.status = builder.status;
    }

    // --- Builder ---
    public static class Builder {
        private String productName;
        private String description;
        private String brandName;
        private BigDecimal rating;
        private long stockQuantity;
        private BigDecimal weight;
        private BigDecimal price;
        private Category category;
        private Status status;

        public Builder productName(String productName) {
            this.productName = productName;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder brandName(String brandName) {
            this.brandName = brandName;
            return this;
        }

        public Builder rating(BigDecimal rating) {
            this.rating = rating;
            return this;
        }

        public Builder stockQuantity(long stockQuantity) {
            this.stockQuantity = stockQuantity;
            return this;
        }

        public Builder weight(BigDecimal weight) {
            this.weight = weight;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder category(Category category) {
            this.category = category;
            return this;
        }

        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        public Product build() {
            return new Product(this);
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
}
