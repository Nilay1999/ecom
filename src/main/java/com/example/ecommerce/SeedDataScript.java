package com.example.ecommerce;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.example.ecommerce.catalog.infra.DataSeeder;

/**
 * Standalone script to manually run data seeding.
 * 
 * Run this using Maven:
 * mvn exec:java
 * -Dexec.mainClass="com.example.ecommerce.catalog.infra.SeedDataScript"
 * -Dspring.profiles.active=local
 * 
 * Or use the provided shell scripts:
 * - Windows: seed-data.bat
 * - Unix/Mac: seed-data.sh
 */
public class SeedDataScript {
    private static final Logger logger = LoggerFactory.getLogger(SeedDataScript.class);

    public static void main(String[] args) {
        logger.info("Starting manual data seeding script...");

        // Start Spring Boot application context
        SpringApplication app = new SpringApplication(ECommerceApplication.class);
        app.setAdditionalProfiles("local");

        ConfigurableApplicationContext context = app.run(args);

        try {
            // Get the DataSeeder bean and run it
            DataSeeder seeder = context.getBean(DataSeeder.class);
            seeder.seedData();

            logger.info("Data seeding completed successfully!");
        } catch (Exception e) {
            logger.error("Error during data seeding: {}", e.getMessage(), e);
            System.exit(1);
        } finally {
            // Close the application context
            context.close();
        }

        System.exit(0);
    }
}
