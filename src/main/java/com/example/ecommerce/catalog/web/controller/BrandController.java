package com.example.ecommerce.catalog.web.controller;

import com.example.ecommerce.catalog.app.BrandService;
import com.example.ecommerce.catalog.domain.Brand;
import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.dto.brand.CreateBrandRequestDto;
import com.example.ecommerce.catalog.dto.brand.UpdateBrandRequestDto;
import com.example.ecommerce.catalog.dto.category.PageResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/brand")
@Tag(name = "Brands", description = "Brands management APIs")
public class BrandController {
    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public ResponseEntity<PageResponseDto<Brand>> getPaginatedBrands(
            @Parameter(description = "Page number (0-based)", example = "0")
                    @RequestParam(defaultValue = "0", name = "page")
                    int page,
            @Parameter(description = "Page size (max 100)", example = "5")
                    @RequestParam(defaultValue = "5", name = "size")
                    int size) {
        return ResponseEntity.ok(brandService.getPaginated(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Brand> getBrandById(@PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(brandService.getBrandById(id));
    }

    @GetMapping("/{id}/product")
    public ResponseEntity<List<Product>> getProductsByBrand(@PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(brandService.getProductsByBrand(id));
    }

    @PostMapping
    public ResponseEntity<Brand> createBrand(@Valid @RequestBody CreateBrandRequestDto request) {
        Brand brand =
                brandService.createBrand(
                        request.name(), request.description(), request.logoUrl(), request.active());
        return ResponseEntity.ok(brand);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Brand> updateBrand(
            @PathVariable(name = "id") UUID id, @Valid @RequestBody UpdateBrandRequestDto request) {
        return ResponseEntity.ok(
                brandService.updateBrand(
                        id,
                        request.name(),
                        request.description(),
                        request.logoUrl(),
                        request.active()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Brand> toggleBrandStatus(
            @PathVariable(name = "id") UUID id, @RequestBody UpdateBrandRequestDto request) {
        return ResponseEntity.ok(brandService.updateBrand(id, request.active()));
    }

    @DeleteMapping
    public ResponseEntity<Boolean> deleteCategoryById(@PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(brandService.deleteBrand(id));
    }
}
