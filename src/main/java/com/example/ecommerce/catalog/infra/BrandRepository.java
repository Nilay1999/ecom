package com.example.ecommerce.catalog.infra;

import com.example.ecommerce.catalog.domain.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface BrandRepository extends JpaRepository<Brand, UUID> {

    @Query("SELECT b FROM Brand b LEFT JOIN FETCH b.products WHERE b.id = :id")
    Optional<Brand> findByIdWithProducts(@Param("id") UUID id);
}
