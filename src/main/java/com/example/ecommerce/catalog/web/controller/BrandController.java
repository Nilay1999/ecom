package com.example.ecommerce.catalog.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.ecommerce.catalog.app.BrandService;
import com.example.ecommerce.catalog.domain.Brand;
import com.example.ecommerce.catalog.web.dto.brand.CreateBrandRequestDto;
import com.example.ecommerce.catalog.web.dto.brand.UpdateBrandRequestDto;

@RestController
@RequestMapping("/brand")
@Tag(name = "Brands", description = "Brands management APIs")
public class BrandController {
    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public ResponseEntity<Page<Brand>> getPaginatedCategories(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        Page<Brand> categories = brandService.getPaginated(page, size);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Brand> getCategoryById(@PathVariable UUID id) {
        return ResponseEntity.ok(brandService.getBrandById(id));
    }

    @PostMapping
    public ResponseEntity<Brand> createBrand(@Valid @RequestBody CreateBrandRequestDto request) {
        Brand brand = brandService.createBrand(request.name(), request.description(), request.logoUrl(),
                request.active());
        return ResponseEntity.ok(brand);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Brand> updateBrand(@PathVariable UUID id, @Valid @RequestBody UpdateBrandRequestDto request) {
        return ResponseEntity.ok(brandService.updateBrand(id, request.name(), request.description(), request.logoUrl(),
                request.active()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Brand> toggleBrandStatus(@PathVariable UUID id, @RequestBody UpdateBrandRequestDto request) {
        return ResponseEntity.ok(brandService.updateBrand(id, request.active()));
    }

    @DeleteMapping
    public ResponseEntity<Boolean> deleteCategoryById(@PathVariable UUID id) {
        return ResponseEntity.ok(brandService.deleteBrand(id));
    }
}
