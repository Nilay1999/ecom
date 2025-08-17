package com.example.ecommerce.catalog.infra;

import com.example.ecommerce.catalog.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {}
