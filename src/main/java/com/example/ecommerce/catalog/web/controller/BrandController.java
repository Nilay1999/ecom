package com.example.ecommerce.catalog.web.controller;

import com.example.ecommerce.catalog.app.BrandService;
import com.example.ecommerce.catalog.domain.Brand;
import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.dto.brand.BrandResponseDto;
import com.example.ecommerce.catalog.dto.brand.CreateBrandRequestDto;
import com.example.ecommerce.catalog.dto.brand.PaginatedBrandsResponseDto;
import com.example.ecommerce.catalog.dto.brand.UpdateBrandRequestDto;
import com.example.ecommerce.catalog.dto.brand.UpdateBrandStatusRequestDto;
import com.example.ecommerce.catalog.dto.category.PageResponseDto;
import com.example.ecommerce.catalog.dto.common.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/brand")
@Tag(name = "Brands", description = "Brands management APIs")
public class BrandController {
    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDto<PaginatedBrandsResponseDto>>> getPaginatedBrands(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0", name = "page") int page,
            @Parameter(description = "Page size (max 100)", example = "5") @RequestParam(defaultValue = "5", name = "size") int size) {
        PageResponseDto<PaginatedBrandsResponseDto> brands = brandService.getPaginated(page, size);
        return ResponseEntity.ok(ApiResponse.success("Brands fetched successfully", brands));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponseDto>> getBrandById(
            @PathVariable(name = "id") UUID id) {
        com.example.ecommerce.catalog.dto.brand.BrandResponseDto brand = brandService.getBrandById(id);
        return ResponseEntity.ok(ApiResponse.success("Brand retrieved successfully", brand));
    }

    @GetMapping("/{id}/product")
    public ResponseEntity<ApiResponse<List<Product>>> getProductsByBrand(@PathVariable(name = "id") UUID id) {
        List<Product> products = brandService.getProductsByBrand(id);
        return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", products));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Brand>> createBrand(@Valid @RequestBody CreateBrandRequestDto request) {
        Brand brand = brandService.createBrand(
                request.name(), request.description(), request.logoUrl(), request.active());
        return ResponseEntity.ok(ApiResponse.created("Brand created successfully", brand));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Brand>> updateBrand(
            @PathVariable(name = "id") UUID id, @Valid @RequestBody UpdateBrandRequestDto request) {
        Brand brand = brandService.updateBrand(
                id,
                request.name(),
                request.description(),
                request.logoUrl(),
                request.active());
        return ResponseEntity.ok(ApiResponse.accepted("Brand updated successfully", brand));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Brand>> toggleBrandStatus(
            @PathVariable(name = "id") UUID id, @RequestBody UpdateBrandStatusRequestDto request) {
        Brand brand = brandService.updateBrand(id, request.active());
        return ResponseEntity.ok(ApiResponse.accepted("Brand status updated successfully", brand));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteCategoryById(@PathVariable(name = "id") UUID id) {
        Boolean deleted = brandService.deleteBrand(id);
        return ResponseEntity.ok(ApiResponse.success("Brand deleted successfully", deleted));
    }
}
