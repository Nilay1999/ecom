package com.example.ecommerce.catalog.infra;

import com.example.ecommerce.catalog.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {}

