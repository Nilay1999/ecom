package com.example.ecommerce.catalog.app;

import com.example.ecommerce.catalog.domain.Brand;
import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.dto.brand.BrandResponseDto;
import com.example.ecommerce.catalog.dto.brand.PaginatedBrandsResponseDto;
import com.example.ecommerce.catalog.dto.category.PageResponseDto;
import com.example.ecommerce.catalog.infra.BrandRepository;
import com.example.ecommerce.common.util.SlugGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BrandService {
    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public BrandResponseDto getBrandById(UUID id) {
        Brand brand = brandRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Brand not found"));
        return new BrandResponseDto(
                brand.getId(),
                brand.getName(),
                brand.getDescription(),
                brand.getLogoUrl(),
                brand.getSlug(),
                brand.isActive(),
                brand.getCreatedAt(),
                brand.getUpdatedAt());
    }

    public List<Product> getProductsByBrand(UUID id) {
        Brand brand = brandRepository
                .findByIdWithProducts(id)
                .orElseThrow(() -> new IllegalArgumentException("Brand not found"));
        return brand.getProducts();
    }

    public PageResponseDto<PaginatedBrandsResponseDto> getPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Brand> brandPage = brandRepository.findAll(pageable);
        List<PaginatedBrandsResponseDto> brandList = brandPage.getContent().stream().map(this::paginatedBrandsDtoMapper)
                .toList();
        return new PageResponseDto<>(
                brandList,
                brandPage.getNumber(),
                brandPage.getSize(),
                brandPage.getTotalElements(),
                brandPage.getTotalPages(),
                brandPage.isLast());
    }

    public Brand createBrand(String name, String description, String logoUrl, Boolean active) {
        String slug = SlugGenerator.generateSlug(name);
        Brand.Builder brandBuilder = new Brand.Builder().setName(name).setSlug(slug).setActive(active);
        return brandRepository.save(brandBuilder.build());
    }

    public Brand updateBrand(
            UUID brandId, String name, String description, String logoUrl, Boolean active) {
        Brand brand = brandRepository
                .findById(brandId)
                .orElseThrow(() -> new RuntimeException("Brand not found"));
        if (name != null && !name.isBlank()) {
            brand.setName(name);
        }
        if (description != null && !description.isBlank()) {
            brand.setDescription(description);
        }
        if (logoUrl != null && !logoUrl.isBlank()) {
            brand.setLogoUrl(logoUrl);
        }
        if (active != null) {
            brand.setActive(active);
        }

        return brandRepository.save(brand);
    }

    public Brand updateBrand(UUID brandId, Boolean active) {
        Brand brand = brandRepository
                .findById(brandId)
                .orElseThrow(() -> new RuntimeException("Brand not found"));
        brand.setActive(active);
        return brandRepository.save(brand);
    }

    public Boolean deleteBrand(UUID id) {
        Brand brand = brandRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));
        brandRepository.delete(brand);
        return true;
    }

    private PaginatedBrandsResponseDto paginatedBrandsDtoMapper(Brand brand) {
        return new PaginatedBrandsResponseDto(brand.getId(), brand.getName(), brand.getDescription(),
                brand.getLogoUrl(), brand.getCreatedAt(), brand.getUpdatedAt());
    }
}
