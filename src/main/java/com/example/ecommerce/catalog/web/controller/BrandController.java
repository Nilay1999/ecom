package com.example.ecommerce.catalog.web.controller;

import com.example.ecommerce.catalog.app.BrandService;
import com.example.ecommerce.catalog.domain.Brand;
import com.example.ecommerce.catalog.web.dto.brand.CreateBrandRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/brand")
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
    public ResponseEntity<Brand> createBrand(@RequestBody CreateBrandRequestDto request) {
        Brand brand = brandService.createBrand(request.getName(), request.getDescription(), request.getLogoUrl(),
                                               request.getActive());
        return ResponseEntity.ok(brand);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Brand> updateBrand(@PathVariable UUID id, @RequestBody CreateBrandRequestDto request) {
        return ResponseEntity.ok(
                brandService.updateBrand(id, request.getName(), request.getDescription(), request.getLogoUrl(),
                                         request.getActive()));
    }

    @DeleteMapping
    public ResponseEntity<Boolean> deleteCategoryById(@PathVariable UUID id) {
        return ResponseEntity.ok(brandService.deleteBrand(id));
    }
}
