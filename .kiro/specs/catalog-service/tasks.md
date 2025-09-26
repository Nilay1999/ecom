# Implementation Plan

- [x] 1. Complete domain entities implementation
  - Implement missing ProductImage entity with domain logic
  - Implement missing ProductVariant entity with business rules
  - Add domain validation methods to existing entities
  - _Requirements: 3.1, 3.2, 3.3, 3.4_

- [x] 2. Create repository interfaces and implementations
  - [x] 2.1 Create ProductImageRepository interface and implementation
    - Define repository interface with custom query methods
    - Implement JPA repository with image-specific queries
    - Add methods for finding primary images and ordering
    - _Requirements: 2.1, 2.2, 2.3_

  - [x] 2.2 Create ProductVariantRepository interface and implementation
    - Define repository interface with variant-specific methods
    - Implement JPA repository with variant queries
    - Add methods for stock and price management
    - _Requirements: 2.1, 2.2, 2.3_

  - [x] 2.3 Enhance existing repositories with missing methods
    - Add custom query methods to ProductRepository
    - Add search and filtering capabilities to CategoryRepository
    - Implement specification pattern for dynamic queries
    - _Requirements: 2.1, 2.2, 2.4_

- [x] 3. Implement service layer components
  - [x] 3.1 Complete ProductService implementation
    - Implement missing CRUD operations with business logic
    - Add product search and filtering methods
    - Implement stock management operations
    - Add product status management
    - _Requirements: 1.1, 1.2, 1.3, 1.4_

  - [x] 3.2 Complete CategoryService implementation
    - Implement missing category operations
    - Add category hierarchy management methods
    - Implement slug generation and validation
    - Add category tree operations
    - _Requirements: 1.1, 1.2, 1.3, 1.4_

  - [x] 3.3 Create ProductImageService
    - Implement image management operations
    - Add primary image management logic
    - Implement image ordering functionality
    - Add image validation methods
    - _Requirements: 1.1, 1.2, 1.3_

  - [x] 3.4 Create ProductVariantService
    - Implement variant CRUD operations
    - Add variant-specific business logic
    - Implement stock management per variant
    - Add price override management
    - _Requirements: 1.1, 1.2, 1.3_

- [x] 4. Create DTO classes and mapping utilities
  - [x] 4.1 Create request DTOs with validation
    - Implement ProductImageRequestDTO with validation annotations
    - Implement ProductVariantRequestDTO with business rules
    - Enhance existing request DTOs with missing fields
    - _Requirements: 4.1, 4.3_

  - [x] 4.2 Create response DTOs
    - Implement ProductImageResponseDTO with proper data exposure
    - Implement ProductVariantResponseDTO with calculated fields
    - Enhance existing response DTOs with missing relationships
    - _Requirements: 4.2, 4.4_

  - [x] 4.3 Create DTO mapping utilities
    - Implement mapper classes for entity-DTO conversion
    - Add mapping methods for complex relationships
    - Implement bidirectional mapping support
    - _Requirements: 4.3_

- [x] 5. Complete controller implementations
  - [x] 5.1 Create ProductImageController
    - Implement CRUD endpoints for product images
    - Add primary image management endpoints
    - Implement image ordering endpoints
    - Add proper HTTP status codes and validation
    - _Requirements: 6.1, 6.4_

  - [x] 5.2 Create ProductVariantController
    - Implement CRUD endpoints for product variants
    - Add variant-specific business operation endpoints
    - Implement stock management endpoints
    - Add bulk operations support
    - _Requirements: 6.1, 6.2, 6.4_

  - [x] 5.3 Enhance existing controllers
    - Add missing endpoints to ProductController
    - Add search and filtering endpoints
    - Implement bulk operations where needed
    - Add proper error handling and validation
    - _Requirements: 6.1, 6.3, 6.4_

- [x] 6. Implement exception handling system
  - [x] 6.1 Create custom exception classes
    - Implement domain-specific exception hierarchy
    - Add ProductNotFoundException and related exceptions
    - Implement validation exception classes
    - _Requirements: 5.1, 5.2, 5.3_

  - [x] 6.2 Enhance global exception handler
    - Add handling for all custom exceptions
    - Implement proper HTTP status code mapping
    - Add detailed error response formatting
    - Implement logging for error tracking
    - _Requirements: 5.1, 5.2, 5.3, 5.4_

- [x] 7. Add utility classes and helper methods
  - [x] 7.1 Create validation utilities
    - Implement business rule validation helpers
    - Add data format validation utilities
    - Create constraint validation helpers
    - _Requirements: 3.4, 5.3_

  - [x] 7.2 Create specification classes for dynamic queries
    - Implement ProductSpecification for complex searches
    - Create CategorySpecification for hierarchy queries
    - Add filtering and sorting specifications
    - _Requirements: 2.2, 1.4_

- [x] 8. Wire everything together and test integration
  - Configure dependency injection for all new components
  - Verify all endpoints work correctly
  - Test complex business operations end-to-end
  - Validate error handling across all layers
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 2.1, 2.2, 2.3, 2.4, 3.1, 3.2, 3.3, 3.4, 4.1, 4.2, 4.3, 4.4, 5.1, 5.2, 5.3, 5.4, 6.1, 6.2, 6.3, 6.4_