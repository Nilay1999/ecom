package com.example.ecommerce.catalog.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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
    @JsonIgnore
    private Product product;

    @Column(nullable = false, length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private boolean isPrimary = false;

    @Column(nullable = false)
    private Integer displayOrder = 0;

    @Column(length = 255)
    private String altText;

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public ProductImage(String imageUrl, boolean isPrimary, Product product) {
        this.imageUrl = imageUrl;
        this.isPrimary = isPrimary;
        this.product = product;
    }

    public ProductImage(String imageUrl, boolean isPrimary, Product product, Integer displayOrder, String altText) {
        this.imageUrl = imageUrl;
        this.isPrimary = isPrimary;
        this.product = product;
        this.displayOrder = displayOrder != null ? displayOrder : 0;
        this.altText = altText;
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
}
