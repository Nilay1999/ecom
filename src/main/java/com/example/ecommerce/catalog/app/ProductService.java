package com.example.ecommerce.catalog.app;

import com.example.ecommerce.catalog.domain.Category;
import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.infra.CategoryRepository;
import com.example.ecommerce.catalog.infra.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;

    public ProductService(ProductRepository productRepo, CategoryRepository categoryRepo) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
    }

    public Product create(String name, String description, BigDecimal price, String brand, BigDecimal weight,
            UUID categoryId) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Product product = new Product.Builder().productName(name)
                .description(description)
                .brandName(brand)
                .price(price)
                .weight(weight)
                .category(category)
                .build();
        return productRepo.save(product);
    }

    public List<Product> findByPriceRange(BigDecimal min, BigDecimal max) {
        return productRepo.findAll()
                .stream()
                .filter(p -> p.getPrice()
                        .compareTo(min) >= 0 && p.getPrice()
                        .compareTo(max) <= 0)
                .toList();
    }
}
