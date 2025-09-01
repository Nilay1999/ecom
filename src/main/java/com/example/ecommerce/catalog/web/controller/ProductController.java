package com.example.ecommerce.catalog.web.controller;

import com.example.ecommerce.catalog.app.ProductService;
import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.web.dto.product.CreateProductRequestDTO;
import com.example.ecommerce.catalog.web.dto.product.CreateProductResponseDTO;
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
    public ResponseEntity<CreateProductResponseDTO> create(@Valid @RequestBody CreateProductRequestDTO request) {
        CreateProductResponseDTO createdProduct;
        if (request.getProductImageList() != null || !request.getProductImageList()
                .isEmpty()) {
            createdProduct = productService.create(request.getProductName(), request.getDescription(),
                    request.getPrice(), request.getBrandName(), request.getWeight(),
                    request.getCategoryId(), request.getProductImageList());
        } else {
            createdProduct = productService.create(request.getProductName(), request.getDescription(),
                    request.getPrice(), request.getBrandName(), request.getWeight(),
                    request.getCategoryId());
        }

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
