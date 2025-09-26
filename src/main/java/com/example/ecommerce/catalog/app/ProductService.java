package com.example.ecommerce.catalog.app;

import com.example.ecommerce.catalog.domain.Category;
import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.domain.ProductImage;
import com.example.ecommerce.catalog.infra.CategoryRepository;
import com.example.ecommerce.catalog.infra.ProductRepository;
import com.example.ecommerce.catalog.web.dto.product.CreateProductResponseDTO;
import com.example.ecommerce.catalog.web.dto.product.ProductImagePayload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public CreateProductResponseDTO create() {
    }

    public CreateProductResponseDTO create() {

    }

    public Page<Product> getPaginatedProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepo.findAll(pageable);
    }

    public Product getProductById(UUID id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
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
