package com.example.ecommerce.catalog.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "product_images")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    private Product product;

    @NotBlank
    @Size(max = 500)
    @Column(nullable = false, length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private boolean isPrimary = false;

    @Column(nullable = false)
    private Integer displayOrder = 0;

    @Size(max = 255)
    @Column(length = 255)
    private String altText;

    @Size(max = 50)
    @Column(length = 50)
    private String imageType = "MAIN"; // MAIN, THUMBNAIL, GALLERY, ZOOM

    @Column(nullable = false)
    private Long fileSize; // in bytes

    @Size(max = 100)
    @Column(length = 100)
    private String mimeType; // image/jpeg, image/png, etc.

    @Column(nullable = false)
    private Integer width; // image width in pixels

    @Column(nullable = false)
    private Integer height; // image height in pixels

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public ProductImage(String imageUrl, boolean isPrimary, Product product) {
        this.imageUrl = imageUrl;
        this.isPrimary = isPrimary;
        this.product = product;
        validateImageUrl();
    }

    public ProductImage(String imageUrl, boolean isPrimary, Product product,
            Integer displayOrder, String altText) {
        this(imageUrl, isPrimary, product);
        this.displayOrder = displayOrder != null ? displayOrder : 0;
        this.altText = altText;
    }

    public ProductImage(String imageUrl, boolean isPrimary, Product product,
            Integer displayOrder, String altText, String imageType,
            Long fileSize, String mimeType, Integer width, Integer height) {
        this(imageUrl, isPrimary, product, displayOrder, altText);
        this.imageType = imageType != null ? imageType : "MAIN";
        this.fileSize = fileSize;
        this.mimeType = mimeType;
        this.width = width;
        this.height = height;
    }

    // Business Methods
    public void markAsPrimary() {
        this.isPrimary = true;
        // If this image is marked as primary, ensure no other images are primary
        if (this.product != null) {
            this.product.getProductImages().stream()
                    .filter(image -> image.isPrimary() && !image.getId().equals(this.id))
                    .forEach(image -> image.setPrimary(false));
        }
    }

    public void setPrimary(boolean isPrimary) {
        if (isPrimary) {
            markAsPrimary();
        } else {
            this.isPrimary = false;
        }
    }

    public void updateDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder != null ? displayOrder : 0;
    }

    public void updateAltText(String altText) {
        this.altText = altText;
    }

    public void updateImageInfo(String imageUrl, String mimeType, Long fileSize,
            Integer width, Integer height) {
        this.imageUrl = imageUrl;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.width = width;
        this.height = height;
        validateImageUrl();
    }

    public boolean isThumbnail() {
        return "THUMBNAIL".equals(this.imageType);
    }

    public boolean isZoomable() {
        return "ZOOM".equals(this.imageType) && this.width != null && this.width > 800;
    }

    public String getImageDimensions() {
        return width + "x" + height;
    }

    public Double getAspectRatio() {
        if (width != null && height != null && height != 0) {
            return (double) width / height;
        }
        return null;
    }

    private void validateImageUrl() {
        if (this.imageUrl == null || this.imageUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Image URL cannot be null or empty");
        }
        if (!this.imageUrl.matches("^(http|https|ftp)://.*$")) {
            throw new IllegalArgumentException("Image URL must be a valid URL");
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        // Auto-generate alt text if not provided
        if (this.altText == null || this.altText.trim().isEmpty()) {
            this.altText = this.product != null ? this.product.getProductName() + " image" : "Product image";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Builder Pattern for better object creation
    public static class Builder {
        private String imageUrl;
        private Product product;
        private boolean isPrimary = false;
        private Integer displayOrder = 0;
        private String altText;
        private String imageType = "MAIN";
        private Long fileSize;
        private String mimeType;
        private Integer width;
        private Integer height;

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder product(Product product) {
            this.product = product;
            return this;
        }

        public Builder isPrimary(boolean isPrimary) {
            this.isPrimary = isPrimary;
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

        public Builder imageType(String imageType) {
            this.imageType = imageType;
            return this;
        }

        public Builder fileSize(Long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public Builder mimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public Builder dimensions(Integer width, Integer height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public ProductImage build() {
            return new ProductImage(imageUrl, isPrimary, product, displayOrder,
                    altText, imageType, fileSize, mimeType, width, height);
        }
    }
}
