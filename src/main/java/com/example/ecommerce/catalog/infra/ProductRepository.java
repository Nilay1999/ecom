package com.example.ecommerce.catalog.infra;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.example.ecommerce.catalog.domain.Brand;
import com.example.ecommerce.catalog.domain.Product;

public interface ProductRepository extends JpaRepository<Product, UUID>,
        JpaSpecificationExecutor<Product> {
    List<Product> findByBrand(Brand brand);

    List<Product> findByBrandId(UUID brandId);
}
