package com.example.ecommerce.catalog.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "brands")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(length = 500)
    private String logoUrl;

    @Column(length = 200, unique = true, nullable = false)
    private String slug;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.PERSIST)
    private final List<Product> products = new ArrayList<>();

    @Column(nullable = false)
    private boolean active = true;

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private Brand(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.logoUrl = builder.logoUrl;
        this.slug = builder.slug;
        this.active = builder.active;
    }

    public static class Builder {
        private String name;
        private String description;
        private String logoUrl;
        private String slug;
        private boolean active = true;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setLogoUrl(String logoUrl) {
            this.logoUrl = logoUrl;
            return this;
        }

        public Builder setSlug(String slug) {
            this.slug = slug;
            return this;
        }

        public Builder setActive(boolean active) {
            this.active = active;
            return this;
        }

        public Brand build() {
            return new Brand(this);
        }
    }

    public void updateName(String name) {
        this.name = name;
        this.slug = generateSlug(name);
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateLogo(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
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

    private String generateSlug(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9]+", "-");
    }
}