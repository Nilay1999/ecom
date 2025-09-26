package com.example.ecommerce.catalog.infra;

import com.example.ecommerce.catalog.domain.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BrandRepository extends JpaRepository<Brand, UUID> {
}
