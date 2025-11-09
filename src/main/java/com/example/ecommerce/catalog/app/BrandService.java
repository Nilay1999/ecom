package com.example.ecommerce.catalog.app;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.ecommerce.catalog.domain.Brand;
import com.example.ecommerce.catalog.domain.Product;
import com.example.ecommerce.catalog.infra.BrandRepository;
import com.example.ecommerce.catalog.infra.ProductRepository;
import com.example.ecommerce.common.util.SlugGenerator;

@Service
@Transactional
public class BrandService {
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    public BrandService(BrandRepository brandRepository, ProductRepository productRepository) {
        this.brandRepository = brandRepository;
        this.productRepository = productRepository;
    }

    public Brand getBrandById(UUID id) {
        return brandRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Brand not found"));
    }

    public List<Product> getProductsByBrand(UUID id) {
        Brand brand = brandRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Brand not found"));
        return productRepository.findByBrand(brand);
    }

    public Page<Brand> getPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return brandRepository.findAll(pageable);
    }

    public Brand createBrand(String name, String description, String logoUrl, Boolean active) {
        String slug = SlugGenerator.generateSlug(name);
        Brand.Builder brandBuilder = new Brand.Builder().setName(name).setSlug(slug).setActive(active);
        return brandRepository.save(brandBuilder.build());
    }

    public Brand updateBrand(UUID brandId, String name, String description, String logoUrl, Boolean active) {
        Brand brand = brandRepository.findById(brandId).orElseThrow(() -> new RuntimeException("Brand not found"));
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
        Brand brand = brandRepository.findById(brandId).orElseThrow(() -> new RuntimeException("Brand not found"));
        brand.setActive(active);
        return brandRepository.save(brand);
    }

    public Boolean deleteBrand(UUID id) {
        Brand brand = brandRepository.findById(id).orElseThrow(() -> new RuntimeException("Brand not found"));
        brandRepository.delete(brand);
        return true;
    }
}
