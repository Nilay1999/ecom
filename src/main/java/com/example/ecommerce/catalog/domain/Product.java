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
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Getter
@Entity
@Table(name = "products", indexes = { @Index(name = "idx_product_name", columnList = "productName"),
        @Index(name = "idx_brand_name", columnList = "brandName") })
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

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariant> productVariants = new ArrayList<>();

    // Domain validation methods
    public static void validateProductName(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }

        if (productName.length() > 255) {
            throw new IllegalArgumentException("Product name cannot exceed 255 characters");
        }
    }

    public static void validateBrandName(String brandName) {
        if (brandName == null || brandName.trim().isEmpty()) {
            throw new IllegalArgumentException("Brand name cannot be null or empty");
        }

        if (brandName.length() > 100) {
            throw new IllegalArgumentException("Brand name cannot exceed 100 characters");
        }
    }

    public static void validateDescription(String description) {
        if (description != null && description.length() > 20000) {
            throw new IllegalArgumentException("Description cannot exceed 20000 characters");
        }
    }

    public static void validatePrice(BigDecimal price) {
        if (price == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }

        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }

        if (price.scale() > 2) {
            throw new IllegalArgumentException("Price cannot have more than 2 decimal places");
        }
    }

    public static void validateWeight(BigDecimal weight) {
        if (weight == null) {
            throw new IllegalArgumentException("Weight cannot be null");
        }

        if (weight.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Weight must be greater than zero");
        }

        if (weight.scale() > 2) {
            throw new IllegalArgumentException("Weight cannot have more than 2 decimal places");
        }
    }

    public static void validateRating(BigDecimal rating) {
        if (rating != null) {
            if (rating.compareTo(BigDecimal.ZERO) < 0 || rating.compareTo(BigDecimal.valueOf(5)) > 0) {
                throw new IllegalArgumentException("Rating must be between 0 and 5");
            }

            if (rating.scale() > 2) {
                throw new IllegalArgumentException("Rating cannot have more than 2 decimal places");
            }
        }
    }

    public static void validateStockQuantity(long stockQuantity) {
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
    }

    // Business logic methods
    public void updateProductName(String newProductName) {
        validateProductName(newProductName);
        this.productName = newProductName.trim();
    }

    public void updateBrandName(String newBrandName) {
        validateBrandName(newBrandName);
        this.brandName = newBrandName.trim();
    }

    public void updateDescription(String newDescription) {
        validateDescription(newDescription);
        this.description = newDescription != null ? newDescription.trim() : null;
    }

    public void updatePrice(BigDecimal newPrice) {
        validatePrice(newPrice);
        this.price = newPrice;

        // Auto-update status based on price and stock
        updateStatusBasedOnBusinessRules();
    }

    public void updateWeight(BigDecimal newWeight) {
        validateWeight(newWeight);
        this.weight = newWeight;
    }

    public void updateRating(BigDecimal newRating) {
        validateRating(newRating);
        this.rating = newRating;
    }

    public void updateStockQuantity(long newStockQuantity) {
        validateStockQuantity(newStockQuantity);
        this.stockQuantity = newStockQuantity;

        // Auto-update status based on stock
        updateStatusBasedOnBusinessRules();
    }

    public void addStock(long quantity) {
        validateStockQuantity(quantity);
        updateStockQuantity(this.stockQuantity + quantity);
    }

    public void removeStock(long quantity) {
        validateStockQuantity(quantity);
        if (quantity > this.stockQuantity) {
            throw new IllegalArgumentException("Cannot remove more stock than available. Available: "
                    + this.stockQuantity + ", Requested: " + quantity);
        }
        updateStockQuantity(this.stockQuantity - quantity);
    }

    public void updateStatus(Status newStatus) {
        Objects.requireNonNull(newStatus, "Status cannot be null");
        this.status = newStatus;
    }

    public void updateCategory(Category newCategory) {
        Objects.requireNonNull(newCategory, "Category cannot be null");
        this.category = newCategory;
    }

    private void updateStatusBasedOnBusinessRules() {
        if (this.stockQuantity == 0) {
            this.status = Status.OUT_OF_STOCK;
        } else if (this.status == Status.OUT_OF_STOCK && this.stockQuantity > 0) {
            this.status = Status.ACTIVE;
        }
    }

    // Image management methods
    public void addImage(ProductImage image) {
        Objects.requireNonNull(image, "Image cannot be null");

        // If this is being set as primary, ensure no other image is primary
        if (image.isPrimary()) {
            ensureOnlyOnePrimaryImage();
        }

        productImages.add(image);
        image.setProduct(this);
    }

    public void removeImage(ProductImage image) {
        Objects.requireNonNull(image, "Image cannot be null");
        productImages.remove(image);
        image.setProduct(null);
    }

    public Optional<ProductImage> getPrimaryImage() {
        return productImages.stream()
                .filter(ProductImage::isPrimary)
                .findFirst();
    }

    public void setPrimaryImage(ProductImage image) {
        Objects.requireNonNull(image, "Image cannot be null");

        if (!productImages.contains(image)) {
            throw new IllegalArgumentException("Image must belong to this product");
        }

        // Remove primary flag from all images
        productImages.forEach(img -> img.markAsSecondary());

        // Set the specified image as primary
        image.markAsPrimary();
    }

    private void ensureOnlyOnePrimaryImage() {
        productImages.forEach(img -> img.markAsSecondary());
    }

    // Variant management methods
    public void addVariant(ProductVariant variant) {
        Objects.requireNonNull(variant, "Variant cannot be null");

        // Check for duplicate variant names
        boolean duplicateName = productVariants.stream()
                .anyMatch(v -> v.getVariantName().equalsIgnoreCase(variant.getVariantName()));

        if (duplicateName) {
            throw new IllegalArgumentException(
                    "Variant with name '" + variant.getVariantName() + "' already exists for this product");
        }

        productVariants.add(variant);
    }

    public void removeVariant(ProductVariant variant) {
        Objects.requireNonNull(variant, "Variant cannot be null");
        productVariants.remove(variant);
    }

    public Optional<ProductVariant> getVariantByName(String variantName) {
        if (variantName == null || variantName.trim().isEmpty()) {
            return Optional.empty();
        }

        return productVariants.stream()
                .filter(v -> v.getVariantName().equalsIgnoreCase(variantName.trim()))
                .findFirst();
    }

    // Business query methods
    public boolean isInStock() {
        return this.stockQuantity > 0;
    }

    public boolean isActive() {
        return this.status == Status.ACTIVE;
    }

    public boolean isAvailable() {
        return isActive() && isInStock();
    }

    public boolean hasVariants() {
        return !productVariants.isEmpty();
    }

    public boolean hasPrimaryImage() {
        return getPrimaryImage().isPresent();
    }

    public long getTotalStock() {
        long productStock = this.stockQuantity;
        long variantStock = productVariants.stream()
                .mapToLong(ProductVariant::getStockQuantity)
                .sum();
        return productStock + variantStock;
    }

    public BigDecimal getLowestPrice() {
        BigDecimal basePrice = this.price;

        BigDecimal lowestVariantPrice = productVariants.stream()
                .filter(ProductVariant::hasPriceOverride)
                .map(ProductVariant::getPriceOverride)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.valueOf(Double.MAX_VALUE));

        if (lowestVariantPrice.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) == 0) {
            return basePrice;
        }

        return basePrice.min(lowestVariantPrice);
    }

    public boolean isValidProduct() {
        try {
            validateProductName(this.productName);
            validateBrandName(this.brandName);
            validateDescription(this.description);
            validatePrice(this.price);
            validateWeight(this.weight);
            validateRating(this.rating);
            validateStockQuantity(this.stockQuantity);

            // Validate category is not null
            Objects.requireNonNull(this.category, "Category cannot be null");

            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
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

        // Validate before persisting
        if (!isValidProduct()) {
            throw new IllegalStateException("Cannot persist invalid Product");
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();

        // Validate before updating
        if (!isValidProduct()) {
            throw new IllegalStateException("Cannot update to invalid Product state");
        }
    }
}
