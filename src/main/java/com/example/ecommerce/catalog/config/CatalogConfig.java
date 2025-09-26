package com.example.ecommerce.catalog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuration class for the Catalog module.
 * Ensures proper component scanning and transaction management.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.example.ecommerce.catalog.infra")
@EnableTransactionManagement
public class CatalogConfig {
    // Configuration is handled through annotations and Spring Boot
    // auto-configuration
    // This class serves as a marker for explicit configuration if needed in the
    // future
}