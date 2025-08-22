package com.example.ecommerce.catalog.web.controller;

import com.example.ecommerce.catalog.app.ProductService;
import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.web.dto.product.CreateProductRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService svc;

    public ProductController(ProductService svc) {
        this.svc = svc;
    }

    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody CreateProductRequest request) {
        Product createdProduct = svc.create(request.getProductName(), request.getDescription(), request.getPrice(),
                                            request.getBrandName(), request.getWeight(), request.getCategoryId());
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @GetMapping
    public List<Product> byPrice(@RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        return svc.findByPriceRange(min, max);
    }
}
