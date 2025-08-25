package com.example.ecommerce.catalog.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Entity
@Getter
@Table(name = "product_image")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    @Column(nullable = false)
    private final Product product;

    @Column(nullable = false, length = 500)
    private final String imageUrl;

    @Column(nullable = false)
    private final boolean isPrimary;

    public ProductImage(String imageUrl, boolean isPrimary, Product product) {
        this.imageUrl = imageUrl;
        this.isPrimary = isPrimary;
        this.product = product;
    }
}
