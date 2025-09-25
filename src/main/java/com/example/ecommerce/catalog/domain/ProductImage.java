package com.example.ecommerce.catalog.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Table(name = "product_images", indexes = {
        @Index(name = "idx_product_image_product_id", columnList = "product_id"),
        @Index(name = "idx_product_image_primary", columnList = "isPrimary"),
        @Index(name = "idx_product_image_display_order", columnList = "displayOrder")
})
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;

    @NotBlank(message = "Image URL cannot be blank")
    @Column(nullable = false, length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private boolean isPrimary = false;

    @NotNull(message = "Display order cannot be null")
    @Column(nullable = false)
    private Integer displayOrder = 0;

    @Column(length = 255)
    private String altText;

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Protected constructor for JPA
    protected ProductImage() {
    }

    // Private constructor for builder
    private ProductImage(Builder builder) {
        validateImageUrl(builder.imageUrl);
        validateDisplayOrder(builder.displayOrder);

        this.imageUrl = builder.imageUrl;
        this.isPrimary = builder.isPrimary;
        this.product = builder.product;
        this.displayOrder = builder.displayOrder != null ? builder.displayOrder : 0;
        this.altText = builder.altText;
    }

    // Legacy constructors for backward compatibility
    public ProductImage(String imageUrl, boolean isPrimary, Product product) {
        this(new Builder()
                .imageUrl(imageUrl)
                .isPrimary(isPrimary)
                .product(product)
                .displayOrder(0));
    }

    public ProductImage(String imageUrl, boolean isPrimary, Product product, Integer displayOrder, String altText) {
        this(new Builder()
                .imageUrl(imageUrl)
                .isPrimary(isPrimary)
                .product(product)
                .displayOrder(displayOrder)
                .altText(altText));
    }

    // Domain validation methods
    public static void validateImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Image URL cannot be null or empty");
        }

        if (imageUrl.length() > 500) {
            throw new IllegalArgumentException("Image URL cannot exceed 500 characters");
        }

        try {
            new URL(imageUrl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid image URL format: " + imageUrl, e);
        }

        // Check for supported image formats
        String lowerUrl = imageUrl.toLowerCase();
        if (!lowerUrl.matches(".*\\.(jpg|jpeg|png|gif|webp|svg)(\\?.*)?$")) {
            throw new IllegalArgumentException(
                    "Unsupported image format. Supported formats: jpg, jpeg, png, gif, webp, svg");
        }
    }

    public static void validateDisplayOrder(Integer displayOrder) {
        if (displayOrder != null && displayOrder < 0) {
            throw new IllegalArgumentException("Display order cannot be negative");
        }
    }

    public static void validateAltText(String altText) {
        if (altText != null && altText.length() > 255) {
            throw new IllegalArgumentException("Alt text cannot exceed 255 characters");
        }
    }

    // Business logic methods
    public void updateImageUrl(String newImageUrl) {
        validateImageUrl(newImageUrl);
        this.imageUrl = newImageUrl;
    }

    public void updateDisplayOrder(Integer newDisplayOrder) {
        validateDisplayOrder(newDisplayOrder);
        this.displayOrder = newDisplayOrder != null ? newDisplayOrder : 0;
    }

    public void updateAltText(String newAltText) {
        validateAltText(newAltText);
        this.altText = newAltText;
    }

    public void markAsPrimary() {
        this.isPrimary = true;
    }

    public void markAsSecondary() {
        this.isPrimary = false;
    }

    public boolean isValidImage() {
        try {
            validateImageUrl(this.imageUrl);
            validateDisplayOrder(this.displayOrder);
            validateAltText(this.altText);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Builder pattern
    public static class Builder {
        private String imageUrl;
        private boolean isPrimary = false;
        private Product product;
        private Integer displayOrder = 0;
        private String altText;

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder isPrimary(boolean isPrimary) {
            this.isPrimary = isPrimary;
            return this;
        }

        public Builder product(Product product) {
            this.product = product;
            return this;
        }

        public Builder displayOrder(Integer displayOrder) {
            this.displayOrder = displayOrder;
            return this;
        }

        public Builder altText(String altText) {
            this.altText = altText;
            return this;
        }

        public ProductImage build() {
            Objects.requireNonNull(product, "Product cannot be null");
            return new ProductImage(this);
        }
    }

    // JPA lifecycle hooks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        // Validate before persisting
        if (!isValidImage()) {
            throw new IllegalStateException("Cannot persist invalid ProductImage");
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();

        // Validate before updating
        if (!isValidImage()) {
            throw new IllegalStateException("Cannot update to invalid ProductImage state");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ProductImage that = (ProductImage) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ProductImage{" +
                "id=" + id +
                ", imageUrl='" + imageUrl + '\'' +
                ", isPrimary=" + isPrimary +
                ", displayOrder=" + displayOrder +
                ", altText='" + altText + '\'' +
                '}';
    }
}
