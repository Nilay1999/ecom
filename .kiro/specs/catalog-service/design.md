# Design Document

## Overview

The Catalog Service will be implemented following SOLID principles and clean architecture patterns. The design focuses on completing missing components while maintaining separation of concerns, dependency inversion, and single responsibility principles.

## Architecture

The catalog service follows a layered architecture:

```
Web Layer (Controllers, DTOs, Exception Handlers)
    ↓
Application Layer (Services, Use Cases)
    ↓
Domain Layer (Entities, Value Objects, Domain Services)
    ↓
Infrastructure Layer (Repositories, External Services)
```

## Components and Interfaces

### 1. Domain Layer Enhancements

**ProductImage Entity**: Complete implementation with proper domain logic
- Image URL validation
- Primary image business rules
- Display order management

**ProductVariant Entity**: Complete implementation with variant-specific logic
- Variant naming and attributes
- Price override validation
- Stock management per variant

**Domain Services**: 
- `ProductDomainService`: Complex product operations
- `CategoryDomainService`: Category hierarchy management

### 2. Application Layer (Services)

**ProductService**: Complete CRUD operations with business logic
- Product creation with validation
- Product updates with business rules
- Product search and filtering
- Stock management operations

**CategoryService**: Complete category management
- Category creation with slug generation
- Hierarchy management
- Category tree operations

**ProductImageService**: Image management operations
- Image upload handling
- Primary image management
- Image ordering operations

**ProductVariantService**: Variant management
- Variant creation and updates
- Stock management per variant
- Price management

### 3. Infrastructure Layer (Repositories)

**Enhanced Repository Interfaces**:
- Custom query methods for complex searches
- Specification pattern for dynamic queries
- Pagination and sorting support

**Repository Implementations**:
- JPA-based implementations
- Query optimization
- Proper relationship handling

### 4. Web Layer (Controllers & DTOs)

**Complete Controller Implementations**:
- Full CRUD operations for all entities
- Proper HTTP status codes
- Request/Response validation

**DTO Structure**:
- Request DTOs with validation annotations
- Response DTOs with proper data exposure
- Mapping utilities between DTOs and domain objects

## Data Models

### Enhanced Product Model
```java
Product {
    - Basic attributes (existing)
    - Enhanced image management
    - Variant relationship management
    - Business rule validation
    - Status management
}
```

### Enhanced Category Model
```java
Category {
    - Hierarchical structure (existing)
    - Enhanced slug management
    - Product relationship management
    - Tree operations support
}
```

### ProductImage Model
```java
ProductImage {
    - URL validation
    - Primary image constraints
    - Display ordering
    - Alt text management
}
```

### ProductVariant Model
```java
ProductVariant {
    - Variant identification
    - Price override logic
    - Stock management
    - Attribute management
}
```

## Error Handling

### Exception Hierarchy
```java
CatalogException (Base)
├── ProductNotFoundException
├── CategoryNotFoundException
├── InvalidProductDataException
├── DuplicateProductException
└── InsufficientStockException
```

### Global Exception Handler
- Consistent error response format
- Proper HTTP status mapping
- Detailed error messages for development
- Sanitized messages for production

## Testing Strategy

While unit tests are excluded for now, the design supports:
- Service layer testing with mocked repositories
- Controller testing with MockMvc
- Integration testing with test containers
- Domain logic testing

## Implementation Patterns

### SOLID Principles Applied

**Single Responsibility Principle (SRP)**:
- Each service handles one domain concept
- Controllers only handle HTTP concerns
- Repositories only handle data access

**Open/Closed Principle (OCP)**:
- Service interfaces allow extension
- Strategy pattern for different operations
- Plugin architecture for new features

**Liskov Substitution Principle (LSP)**:
- Repository implementations are interchangeable
- Service implementations follow contracts

**Interface Segregation Principle (ISP)**:
- Focused interfaces for specific operations
- No fat interfaces with unused methods

**Dependency Inversion Principle (DIP)**:
- Services depend on repository interfaces
- Controllers depend on service interfaces
- Configuration through dependency injection

### Design Patterns Used

1. **Repository Pattern**: Data access abstraction
2. **Builder Pattern**: Complex object construction
3. **Factory Pattern**: Entity creation
4. **Specification Pattern**: Dynamic query building
5. **DTO Pattern**: Data transfer optimization
6. **Service Layer Pattern**: Business logic encapsulation