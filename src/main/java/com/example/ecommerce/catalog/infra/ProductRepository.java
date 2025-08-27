package com.example.ecommerce.catalog.infra;

import com.example.ecommerce.catalog.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}

