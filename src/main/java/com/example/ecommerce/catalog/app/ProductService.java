package com.example.ecommerce.catalog.app;

import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.infra.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepo;

    public ProductService(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    public Product create(String name, String description, BigDecimal price, String brand) {
        Product p =
                new Product.Builder().productName(name).description(description).brandName(brand).price(price).build();
        return productRepo.save(p);
    }

    public List<Product> findByPriceRange(BigDecimal min, BigDecimal max) {
        return productRepo.findAll().stream().filter(p -> p.getPrice().compareTo(min) >= 0 && p.getPrice().compareTo(max) <= 0).toList();
    }
}
