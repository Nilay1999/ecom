package com.example.ecommerce.catalog.web.controller;

import com.example.ecommerce.catalog.app.ProductService;
import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.web.dto.product.CreateProductRequestDto;
import com.example.ecommerce.catalog.web.dto.product.CreateProductResponseDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<CreateProductResponseDto> create(@Valid @RequestBody CreateProductRequestDto request) {
        CreateProductResponseDto createdProduct = productService.create(request.productName(), request.description(),
                                                                        request.price(), request.brandId(),
                                                                        request.categoryId(), request.color(),
                                                                        request.rating(), request.status(),
                                                                        request.size(), request.color(),
                                                                        request.stockQuantity(), request.weight());
        
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<Product>> getPaginatedProducts(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Product> products = productService.getPaginatedProducts(page, size);
        return ResponseEntity.ok(products);
    }

    @GetMapping(":id")
    public ResponseEntity<Product> getProductById(@PathVariable UUID id) {
        Product product = productService.getProductById(id);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @GetMapping("/byPrice")
    public List<Product> byPrice(@RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        return productService.findByPriceRange(min, max);
    }
}
