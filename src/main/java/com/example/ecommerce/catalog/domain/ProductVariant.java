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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Table(name = "product_variants", indexes = {
        @Index(name = "idx_product_variant_product_id", columnList = "product_id"),
        @Index(name = "idx_product_variant_name", columnList = "variantName"),
        @Index(name = "idx_product_variant_stock", columnList = "stockQuantity")
})
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;

    @NotBlank(message = "Variant name cannot be blank")
    @Column(nullable = false, length = 100)
    private String variantName;

    @DecimalMin(value = "0.00", inclusive = true, message = "Price override must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Price override must have at most 10 integer digits and 2 decimal places")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal priceOverride = BigDecimal.ZERO;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    @Column(nullable = false)
    private long stockQuantity = 0;

    // Store variant attributes as JSON or key-value pairs
    @ElementCollection
    @CollectionTable(name = "product_variant_attributes", joinColumns = @JoinColumn(name = "variant_id"))
    @MapKeyColumn(name = "attribute_name")
    @Column(name = "attribute_value")
    private Map<String, String> attributes = new HashMap<>();

    public enum Status {
        ACTIVE, INACTIVE, OUT_OF_STOCK, DISCONTINUED
    }

    @Column(name = "status", length = 25, nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @Column(length = 50)
    private String sku; // Stock Keeping Unit

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Protected constructor for JPA
    protected ProductVariant() {
    }

    // Private constructor for builder
    private ProductVariant(Builder builder) {
        validateVariantName(builder.variantName);
        validatePriceOverride(builder.priceOverride);
        validateStockQuantity(builder.stockQuantity);
        validateSku(builder.sku);

        this.product = builder.product;
        this.variantName = builder.variantName;
        this.priceOverride = builder.priceOverride != null ? builder.priceOverride : BigDecimal.ZERO;
        this.stockQuantity = builder.stockQuantity;
        this.attributes = builder.attributes != null ? new HashMap<>(builder.attributes) : new HashMap<>();
        this.status = builder.status != null ? builder.status : Status.ACTIVE;
        this.sku = builder.sku;
    }

    // Domain validation methods
    public static void validateVariantName(String variantName) {
        if (variantName == null || variantName.trim().isEmpty()) {
            throw new IllegalArgumentException("Variant name cannot be null or empty");
        }

        if (variantName.length() > 100) {
            throw new IllegalArgumentException("Variant name cannot exceed 100 characters");
        }

        // Check for valid characters (alphanumeric, spaces, hyphens, underscores)
        if (!variantName.matches("^[a-zA-Z0-9\\s\\-_]+$")) {
            throw new IllegalArgumentException(
                    "Variant name contains invalid characters. Only alphanumeric characters, spaces, hyphens, and underscores are allowed");
        }
    }

    public static void validatePriceOverride(BigDecimal priceOverride) {
        if (priceOverride != null && priceOverride.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price override cannot be negative");
        }

        if (priceOverride != null && priceOverride.scale() > 2) {
            throw new IllegalArgumentException("Price override cannot have more than 2 decimal places");
        }
    }

    public static void validateStockQuantity(long stockQuantity) {
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
    }

    public static void validateSku(String sku) {
        if (sku != null && sku.length() > 50) {
            throw new IllegalArgumentException("SKU cannot exceed 50 characters");
        }

        if (sku != null && !sku.trim().isEmpty() && !sku.matches("^[a-zA-Z0-9\\-_]+$")) {
            throw new IllegalArgumentException(
                    "SKU contains invalid characters. Only alphanumeric characters, hyphens, and underscores are allowed");
        }
    }

    public static void validateAttributeName(String attributeName) {
        if (attributeName == null || attributeName.trim().isEmpty()) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }

        if (attributeName.length() > 50) {
            throw new IllegalArgumentException("Attribute name cannot exceed 50 characters");
        }
    }

    public static void validateAttributeValue(String attributeValue) {
        if (attributeValue != null && attributeValue.length() > 255) {
            throw new IllegalArgumentException("Attribute value cannot exceed 255 characters");
        }
    }

    // Business logic methods
    public void updateVariantName(String newVariantName) {
        validateVariantName(newVariantName);
        this.variantName = newVariantName.trim();
    }

    public void updatePriceOverride(BigDecimal newPriceOverride) {
        validatePriceOverride(newPriceOverride);
        this.priceOverride = newPriceOverride != null ? newPriceOverride : BigDecimal.ZERO;
    }

    public void updateStockQuantity(long newStockQuantity) {
        validateStockQuantity(newStockQuantity);
        this.stockQuantity = newStockQuantity;

        // Auto-update status based on stock
        if (newStockQuantity == 0 && this.status == Status.ACTIVE) {
            this.status = Status.OUT_OF_STOCK;
        } else if (newStockQuantity > 0 && this.status == Status.OUT_OF_STOCK) {
            this.status = Status.ACTIVE;
        }
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

    public void updateSku(String newSku) {
        validateSku(newSku);
        this.sku = newSku != null ? newSku.trim() : null;
    }

    public void addAttribute(String name, String value) {
        validateAttributeName(name);
        validateAttributeValue(value);
        this.attributes.put(name.trim(), value != null ? value.trim() : null);
    }

    public void removeAttribute(String name) {
        validateAttributeName(name);
        this.attributes.remove(name.trim());
    }

    public String getAttribute(String name) {
        validateAttributeName(name);
        return this.attributes.get(name.trim());
    }

    public boolean hasAttribute(String name) {
        validateAttributeName(name);
        return this.attributes.containsKey(name.trim());
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

    public BigDecimal getEffectivePrice() {
        if (this.priceOverride != null && this.priceOverride.compareTo(BigDecimal.ZERO) > 0) {
            return this.priceOverride;
        }
        return this.product != null ? this.product.getPrice() : BigDecimal.ZERO;
    }

    public boolean hasPriceOverride() {
        return this.priceOverride != null && this.priceOverride.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isValidVariant() {
        try {
            validateVariantName(this.variantName);
            validatePriceOverride(this.priceOverride);
            validateStockQuantity(this.stockQuantity);
            validateSku(this.sku);

            // Validate all attributes
            for (Map.Entry<String, String> entry : this.attributes.entrySet()) {
                validateAttributeName(entry.getKey());
                validateAttributeValue(entry.getValue());
            }

            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Builder pattern
    public static class Builder {
        private Product product;
        private String variantName;
        private BigDecimal priceOverride;
        private long stockQuantity = 0;
        private Map<String, String> attributes;
        private Status status;
        private String sku;

        public Builder product(Product product) {
            this.product = product;
            return this;
        }

        public Builder variantName(String variantName) {
            this.variantName = variantName;
            return this;
        }

        public Builder priceOverride(BigDecimal priceOverride) {
            this.priceOverride = priceOverride;
            return this;
        }

        public Builder stockQuantity(long stockQuantity) {
            this.stockQuantity = stockQuantity;
            return this;
        }

        public Builder attributes(Map<String, String> attributes) {
            this.attributes = attributes;
            return this;
        }

        public Builder addAttribute(String name, String value) {
            if (this.attributes == null) {
                this.attributes = new HashMap<>();
            }
            this.attributes.put(name, value);
            return this;
        }

        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        public Builder sku(String sku) {
            this.sku = sku;
            return this;
        }

        public ProductVariant build() {
            Objects.requireNonNull(product, "Product cannot be null");
            Objects.requireNonNull(variantName, "Variant name cannot be null");
            return new ProductVariant(this);
        }
    }

    // JPA lifecycle hooks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        // Validate before persisting
        if (!isValidVariant()) {
            throw new IllegalStateException("Cannot persist invalid ProductVariant");
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();

        // Validate before updating
        if (!isValidVariant()) {
            throw new IllegalStateException("Cannot update to invalid ProductVariant state");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ProductVariant that = (ProductVariant) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ProductVariant{" +
                "id=" + id +
                ", variantName='" + variantName + '\'' +
                ", priceOverride=" + priceOverride +
                ", stockQuantity=" + stockQuantity +
                ", status=" + status +
                ", sku='" + sku + '\'' +
                '}';
    }
}
