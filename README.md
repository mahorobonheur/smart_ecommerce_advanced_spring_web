# Smart E-Commerce Backend - Spring Web Application

## Project Overview

This is a smart e-commerce backend system built with modern Spring technologies. It serves as a feature-rich platform demonstrating advanced Spring concepts in a practical, real-world context. The project implements a complete e-commerce system with user management, product catalog, and orders.

## Key Features

- **Dual API Support**: Both RESTful APIs and GraphQL endpoints for flexible client integration
- **Environment Profiling**: Separate configurations for development, testing, and production environments
- **Aspect-Oriented Programming**: Cross-cutting concerns handled through Spring AOP
- **Complete E-Commerce Flow**: User management, product catalog, shopping cart, orders, and payments
- **Advanced Error Handling**: Structured error responses and validation

## Technology Stack

- **Java 21** - Core programming language
- **Spring Boot 4.x** - Application framework
- **Spring Data JPA** - Database persistence
- **GraphQL** - Alternative API layer
- **H2 Database** - In-memory database for development
- **Maven** - Dependency management
- **Spring AOP** - Aspect-oriented programming

## Getting Started

### Prerequisites

Before you begin, make sure you have:
- JDK 21 or higher installed
- Maven 3.6 or higher
- Your favorite IDE (IntelliJ IDEA, Eclipse, or VS Code)

### Installation Steps

1. Clone the repository:
```bash
git clone https://github.com/mahorobonheur/smart_ecommerce_advanced_spring_web.git
```

2. Navigate to the project directory:
```bash
cd smart_ecommerce_advanced_spring_web
```

3. Build the project:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn spring-boot:run
```

By default, the application starts on `http://localhost:8080`

## Project Structure

The project follows a standard Spring Boot layered architecture:

```
src/main/java/com/smart/ecommerce/
├── controller/rest/    # REST GraphQL endpoints
├── service/        # Business logic layer
├── repository/     # Data access layer
├── model/          # Entity classes
├── dto/            # Data transfer objects
├── config/         # Application configuration
└── aspect/         # AOP components
├── graphql         # GraphQL endpoints
├── exception        #Exception Handling
```

## Environment Profiles

The application supports multiple environments:

- **Development** (`dev`): Default profile with H2 database and sample data
- **Production** (`prod`): Configured for production deployment
- **Testing** (`test`): Setup for integration and unit tests

Select a profile using:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## API Documentation

### REST API Endpoints

- `GET /api/products` - Retrieve all products
- `GET /api/products/{id}` - Get specific product details
- `POST /api/orders` - Create a new order
- `GET /api/users/{userId}/orders` - Get user's order history
And more.

### GraphQL Endpoint

Access the GraphQL playground at `http://localhost:8080/graphiql` when the application is running. This provides an interactive interface to explore and test GraphQL queries and mutations.

## Aspect-Oriented Programming

The project demonstrates practical AOP usage through:

- **Logging Aspect**: Automatic method invocation logging
- **Performance Monitoring**: Execution time tracking for critical methods

## Development Notes

## 1. Repository Usage Strategy
## 1.1 General Rules

JPA repositories are used for relational data (User, Product, Order, Inventory, Category, Cart).

Mongo repositories are used for document-style, high‑write data (Reviews).

Repositories never contain business logic.

Complex filtering/searching uses Specifications, not custom queries.

Custom @Query is used only for:

Reports

Aggregations

Performance‑critical queries

## 2. Repository Breakdown & Query Logic
## 2.1 UserRepository (JPA)

Purpose: Authentication, uniqueness checks, global search.

boolean existsByEmail(String email);
Optional<User> findByEmail(String email);

Uses derived queries (simple & readable).

Specifications used for global search (UserSpecification).

## 2.2 ProductRepository (JPA)

Purpose: Product browsing, filtering, reporting.

Page<Product> findByCategory_CategoryName(String categoryName, Pageable pageable);
Page<Product> findByPriceBetween(double min, double max, Pageable pageable);
Low‑stock report query
@Query("""
    SELECT p FROM Product p
    JOIN p.inventory i
    WHERE i.quantityAvailable < :threshold
    ORDER BY i.quantityAvailable ASC
""")
Page<Product> findProductLowOnStock(@Param("threshold") int threshold, Pageable pageable);

Why @Query?

Cross‑entity join (Product + Inventory)

Reporting use‑case

Sorting by joined entity field

## 2.3 InventoryRepository (JPA)

Purpose: Stock tracking.

boolean existsByProduct_ProductId(UUID productId);
Inventory findByProduct_ProductId(UUID productId);

Ensures 1‑to‑1 product–inventory constraint.

Used heavily in order processing and stock validation.

## 2.4 CategoryRepository (JPA)

Purpose: Product classification.

boolean existsByCategoryName(String categoryName);

Prevents duplicate categories.

## 2.5 CartRepository (JPA)

Purpose: User shopping cart.

Optional<Cart> findByUser(User user);

One cart per user.

Lazy‑loaded items.

## 2.6 ReviewRepository (MongoDB)

Purpose: High‑volume, write‑heavy user reviews.

List<Review> findByProductId(String productId);

Why MongoDB?

Reviews are independent documents

Frequent writes

No joins required

## 3. Transaction Management Strategy
## 3.1 Global Rules

Service layer owns transactions

Repositories are transaction‑agnostic

Default propagation: REQUIRED

Read‑only queries explicitly marked

## 3.2 Read Transactions
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)

Used for:

Fetching products

Pagination

Reports

Benefits:

No dirty checks

Faster performance

Clear intent

## 3.3 Write Transactions
@Transactional(
    rollbackFor = Exception.class,
    noRollbackFor = IllegalArgumentException.class
)

Used for:

Create / update / delete operations

Checkout & order creation

Why custom rollback rules?

Validation errors should not rollback DB state

Business failures should rollback

## 3.4 Critical Transactions (Orders)

Order checkout & confirmation:

Payment validation

Stock validation

Inventory update

Order + OrderItems creation

All executed in single transaction to ensure:

Atomicity

Consistency

## 4. Caching Strategy
## 4.1 General Principles

Cache read‑heavy operations

Never cache writes

Always evict on data mutation

Cache keys must include all method parameters

## 5. Cache Usage by Domain
## 5.1 Product Cache
Cache	Purpose
productById	Product detail page
productsPage	Product listing
productsByCategory	Category browsing
lowStockProducts	Inventory reports
Low‑stock cache example
@Cacheable(
  value = "lowStockProducts",
  key = "'threshold:' + #threshold + '-page:' + #pageable.pageNumber"
)

Evicted when:

Product updated

Inventory updated

Product deleted

## 5.2 Inventory Cache
Cache	Purpose
inventoryById	Inventory lookup
inventoryByProduct	Product stock check
inventoriesPage	Inventory admin view

Eviction triggered on:

Stock update

Inventory deletion

## 5.3 Cart Cache
Cache	Purpose
cartByUser	Fast cart retrieval

Evicted on:

Add item

Remove item

Clear cart

## 5.4 Order Cache
Cache	Purpose
orderById	Order detail
ordersPage	Admin order list

Evicted on:

Order creation

Status update

Deletion

## 5.5 Review Cache
Cache	Purpose
reviewsPage	Paginated reviews
reviewsByProduct	Product reviews

Evicted on:

Review add

Review update

Review delete

## 6. Cache Eviction Rules (Golden Rules)

Create → evict lists

Update → evict item + lists

Delete → evict everything related

Cache keys must include filters (page, size, threshold, category)

## 7. Extension Guidelines

When adding a new feature:

Decide storage (JPA vs Mongo)

Keep repository queries simple

Use Specifications for search

Define transaction boundaries in service

Cache only if:

Data is read frequently

Data changes infrequently

## Contributing

This project serves as a learning resource for Spring Boot advanced features. Feel free to explore the code, experiment with modifications, and adapt the patterns to your own projects.

## Learning Outcomes

By examining this codebase, developers can learn:

- How to implement both REST and GraphQL in a single application
- Practical application of Spring Profiles for environment-specific configuration
- Real-world use cases for Aspect-Oriented Programming
- Best practices for structuring a medium-sized Spring Boot application
- Domain modeling for e-commerce systems

The application is ready to run and explore. Start with the development profile to see the system with sample data, then examine how different components interact to provide a complete e-commerce backend solution.
