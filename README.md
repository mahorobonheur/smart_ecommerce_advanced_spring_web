# Smart E-Commerce Backend - Spring Web Application

## Project Overview

This is a smart e-commerce backend system built with modern Spring technologies. It serves as a feature-rich platform demonstrating advanced Spring concepts in a practical, real-world context. The project implements a complete e-commerce system with user management, product catalog, orders, and payment processing.

## Key Features

- **Dual API Support**: Both RESTful APIs and GraphQL endpoints for flexible client integration
- **Environment Profiling**: Separate configurations for development, testing, and production environments
- **Aspect-Oriented Programming**: Cross-cutting concerns handled through Spring AOP
- **Complete E-Commerce Flow**: User management, product catalog, shopping cart, orders, and payments
- **Advanced Error Handling**: Structured error responses and validation

## Technology Stack

- **Java 17** - Core programming language
- **Spring Boot 3.x** - Application framework
- **Spring Data JPA** - Database persistence
- **Spring Security** - Authentication and authorization
- **GraphQL** - Alternative API layer
- **H2 Database** - In-memory database for development
- **Maven** - Dependency management
- **Spring AOP** - Aspect-oriented programming

## Getting Started

### Prerequisites

Before you begin, make sure you have:
- JDK 17 or higher installed
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
├── controller/     # REST and GraphQL endpoints
├── service/        # Business logic layer
├── repository/     # Data access layer
├── model/          # Entity classes
├── dto/            # Data transfer objects
├── config/         # Application configuration
└── aspect/         # AOP components
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

### GraphQL Endpoint

Access the GraphQL playground at `http://localhost:8080/graphiql` when the application is running. This provides an interactive interface to explore and test GraphQL queries and mutations.

## Aspect-Oriented Programming

The project demonstrates practical AOP usage through:

- **Logging Aspect**: Automatic method invocation logging
- **Performance Monitoring**: Execution time tracking for critical methods
- **Transaction Management**: Consistent transaction handling
- **Security Auditing**: Method-level security audit trails

## Development Notes

The project includes a comprehensive implementation of an e-commerce domain model with entities for User, Product, Order, OrderItem, and Payment. The code demonstrates best practices in Spring development including proper layering, dependency injection, and exception handling.

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
