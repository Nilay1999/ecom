package com.example.ecommerce.catalog.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class DummyProduct {
    private int id;
    private String title;
    private String description;
    private String category;
    private BigDecimal price;
    private double discountPercentage;
    private double rating;
    private Long stock;
    private List<String> tags;
    private String brand;
    private String sku;
    private BigDecimal weight;
    private Dimensions dimensions;
    private String warrantyInformation;
    private String shippingInformation;
    private String availabilityStatus;
    private List<Review> reviews;
    private String returnPolicy;
    private int minimumOrderQuantity;
    private Meta meta;
    private List<String> images;
    private String thumbnail;

    public String getBrand() {
        if (brand == null) {
            return category;
        }
        return brand;
    }

    // --- Nested Classes ---
    @Setter
    @Getter
    public static class Dimensions {
        // Getters and setters
        private double width;
        private double height;
        private double depth;

    }

    @Setter
    @Getter
    public static class Review {
        // Getters and setters
        private int rating;
        private String comment;
        private LocalDateTime date;
        private String reviewerName;
        private String reviewerEmail;

    }

    @Setter
    @Getter
    public static class Meta {
        // Getters and setters
        private String createdAt;
        private String updatedAt;
        private String barcode;
        private String qrCode;
    }
}
