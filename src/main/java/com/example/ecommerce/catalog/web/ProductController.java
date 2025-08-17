package com.example.ecommerce.catalog.web;

import com.example.ecommerce.catalog.app.ProductService;
import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.web.dto.CreateProductRequest;
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
    public Product create(@RequestBody CreateProductRequest request) {
        return svc.create(request.getProductName(), request.getDescription(), request.getPrice(),
                request.getBrandName());
    }

    @GetMapping
    public List<Product> byPrice(@RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        return svc.findByPriceRange(min, max);
    }
}
