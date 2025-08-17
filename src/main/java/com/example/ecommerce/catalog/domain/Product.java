package com.example.ecommerce.catalog.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "products")
public class Product {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Getter
    private String productName;
    @Getter
    private String description;
    @Getter
    private String brandName;

    @Column(precision = 10, scale = 2) // e.g., 99999999.99 max
    @Getter
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private LocalDateTime updatedAt;

    protected Product() {
    }

    private Product(Builder builder) {
        this.productName = builder.productName;
        this.brandName = builder.brandName;
        this.price = builder.price;
        this.description = builder.description;
        this.category = builder.category;
    }

    // --- Builder ---
    public static class Builder {
        private String productName;
        private String description;
        private String brandName;
        private BigDecimal price;
        private Category category;

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

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder category(Category category) {
            this.category = category;
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
